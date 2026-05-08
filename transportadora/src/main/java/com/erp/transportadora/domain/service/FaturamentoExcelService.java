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
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 7000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);

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
            String[] titulos = {"Data", "Notas Fiscais", "Valor Merc.", "Volume", "Destino", "Cidade", "Frete Valor"};
            for (int i = 0; i < titulos.length; i++) {
                Cell c = tituloRow.createCell(i);
                c.setCellValue(titulos[i]);
                c.setCellStyle(estiloHeader);
            }

            List<Object[]> notas = faturamentoService.buscarNotasDoCliente(clienteSelecionado.clienteId(), dataInicio, dataFim);
            double totalValor = 0;
            double totalFrete = 0;
            int totalVolume = 0;

            for (Object[] nota : notas) {
                Row linhaRow = sheet.createRow(rowNum++);

                String dataStr = formatarData(nota[0]);
                String numero = nota[1] != null ? (String) nota[1] : "";
                Double valor = nota[2] != null ? ((Number) nota[2]).doubleValue() : 0.0;
                Integer volume = nota[3] != null ? ((Number) nota[3]).intValue() : 0;
                String dest = nota[4] != null ? (String) nota[4] : "";
                String cid = nota[5] != null ? (String) nota[5] : "";
                Double frete = nota[6] != null ? ((Number) nota[6]).doubleValue() : 0.0;

                criarCelula(linhaRow, 0, dataStr, estiloLinha);
                criarCelula(linhaRow, 1, numero, estiloLinha);
                criarCelulaComEstilo(linhaRow, 2, valor, estiloMoeda);
                criarCelula(linhaRow, 3, String.valueOf(volume), estiloTotal);
                criarCelula(linhaRow, 4, dest, estiloLinha);
                criarCelula(linhaRow, 5, cid, estiloLinha);
                criarCelulaComEstilo(linhaRow, 6, frete, estiloMoeda);

                totalValor += valor;
                totalFrete += frete;
                totalVolume += volume;
            }

            rowNum++;
            Row totalRow = sheet.createRow(rowNum++);
            criarCelula(totalRow, 0, "TOTAL", estiloTotal);
            criarCelula(totalRow, 1, "", estiloTotal);
            criarCelulaComEstilo(totalRow, 2, totalValor, estiloTotal);
            criarCelula(totalRow, 3, String.valueOf(totalVolume), estiloTotal);
            criarCelula(totalRow, 4, "", estiloTotal);
            criarCelula(totalRow, 5, "", estiloTotal);
            criarCelulaComEstilo(totalRow, 6, totalFrete, estiloTotal);

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