package com.erp.transportadora.domain.service;

import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaturamentoExcelService {

    private final FaturamentoService faturamentoService;

    public byte[] gerarExcel(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        List<FaturamentoDTOClienteResponse> todos = faturamentoService.listar(dataInicio, dataFim);
        FaturamentoDTOClienteResponse clienteSelecionado = todos.stream()
                .filter(c -> c.clienteId().equals(clienteId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {


            XSSFCellStyle estiloTitulo = criarEstilo(workbook, new byte[]{(byte) 200, (byte) 200, (byte) 200}, true, 11);
            XSSFCellStyle estiloHeader = criarEstilo(workbook, new byte[]{(byte) 180, (byte) 180, (byte) 180}, true, 10);
            XSSFCellStyle estiloLinha = criarEstilo(workbook, null, false, 10);
            XSSFCellStyle estiloTotal = criarEstilo(workbook, new byte[]{(byte) 220, (byte) 220, (byte) 220}, true, 10);

            XSSFCellStyle estiloMoeda = workbook.createCellStyle();
            estiloMoeda.cloneStyleFrom(estiloLinha);
            estiloMoeda.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));

            String nomeAba = clienteSelecionado.cliente().length() > 31
                    ? clienteSelecionado.cliente().substring(0, 31) : clienteSelecionado.cliente();

            nomeAba = nomeAba.replaceAll("[\\*\\?\\[\\]\\/\\\\:]", " ");

            XSSFSheet sheet = workbook.createSheet(nomeAba);

            // Configuração de colunas
            sheet.setColumnWidth(0, 3500);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 7000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);

            int rowNum = 0;

            Row cabecalho = sheet.createRow(rowNum++);
            Cell cellNome = cabecalho.createCell(0);
            cellNome.setCellValue(clienteSelecionado.cliente());
            cellNome.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row periodoRow = sheet.createRow(rowNum++);
            String periodo = (dataInicio != null && dataFim != null)
                    ? "Período: " + dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "Período: Todos";
            cellPeriodo(periodoRow, periodo, estiloLinha, sheet, rowNum - 1);

            rowNum++;

            Row tituloRow = sheet.createRow(rowNum++);
            String[] titulos = {"Data", "Notas Fiscais", "Valor Merc.", "Destino", "Cidade", "Frete Valor"};
            for (int i = 0; i < titulos.length; i++) {
                Cell c = tituloRow.createCell(i);
                c.setCellValue(titulos[i]);
                c.setCellStyle(estiloHeader);
            }

            List<Object[]> notas = faturamentoService.buscarNotasDoCliente(clienteSelecionado.clienteId(), dataInicio, dataFim);
            double totalValor = 0;
            double totalFrete = 0;

            for (Object[] nota : notas) {
                Row linhaRow = sheet.createRow(rowNum++);

                String dataStr = formatarData(nota[0]);
                String numero = nota[1] != null ? (String) nota[1] : "";
                Double valor = nota[2] != null ? ((Number) nota[2]).doubleValue() : 0.0;
                String dest = nota[3] != null ? (String) nota[3] : "";
                String cid = nota[4] != null ? (String) nota[4] : "";
                Double frete = nota[5] != null ? ((Number) nota[5]).doubleValue() : 0.0;

                criarCelula(linhaRow, 0, dataStr, estiloLinha);
                criarCelula(linhaRow, 1, numero, estiloLinha);
                criarCelulaComEstilo(linhaRow, 2, valor, estiloMoeda); // Usando estilo reaproveitado
                criarCelula(linhaRow, 3, dest, estiloLinha);
                criarCelula(linhaRow, 4, cid, estiloLinha);
                criarCelulaComEstilo(linhaRow, 5, frete, estiloMoeda); // Usando estilo reaproveitado

                totalValor += valor;
                totalFrete += frete;
            }

            rowNum++;
            Row totalRow = sheet.createRow(rowNum++);
            criarCelula(totalRow, 0, "TOTAL", estiloTotal);
            criarCelula(totalRow, 1, "", estiloTotal);
            criarCelulaComEstilo(totalRow, 2, totalValor, estiloTotal); // Aqui pode ser o total ou um novo estilo moeda-negrito
            criarCelula(totalRow, 3, "", estiloTotal);
            criarCelula(totalRow, 4, "", estiloTotal);
            criarCelulaComEstilo(totalRow, 5, totalFrete, estiloTotal);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    private String formatarData(Object obj) {
        if (obj == null) return "";
        if (obj instanceof java.sql.Timestamp ts)
            return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (obj instanceof LocalDateTime ldt) return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return obj.toString();
    }

    private void criarCelulaComEstilo(Row row, int col, Double valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : 0.0);
        cell.setCellStyle(estilo);
    }

    private void cellPeriodo(Row row, String texto, CellStyle estilo, XSSFSheet sheet, int rowIdx) {
        Cell cell = row.createCell(0);
        cell.setCellValue(texto);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 5));
    }

    private void criarCelula(Row row, int col, String valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : "");
        cell.setCellStyle(estilo);
    }

    private XSSFCellStyle criarEstilo(XSSFWorkbook workbook, byte[] corFundo, boolean negrito, int fontSize) {
        XSSFCellStyle estilo = workbook.createCellStyle();

        if (corFundo != null) {
            XSSFColor cor = new XSSFColor(corFundo, null);
            estilo.setFillForegroundColor(cor);
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        XSSFFont fonte = workbook.createFont();
        fonte.setBold(negrito);
        fonte.setFontHeightInPoints((short) fontSize);
        estilo.setFont(fonte);

        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        return estilo;
    }
}