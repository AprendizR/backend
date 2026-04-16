package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.dto.response.FaturamentoDTOClienteResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

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

    public byte[] gerarExcel(LocalDate dataInicio, LocalDate dataFim) {
        List<FaturamentoDTOClienteResponse> clientes = faturamentoService.listar(dataInicio, dataFim);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            for (FaturamentoDTOClienteResponse cliente : clientes) {
                String nomeAba = cliente.cliente().length() > 31
                        ? cliente.cliente().substring(0, 31) : cliente.cliente();
                XSSFSheet sheet = workbook.createSheet(nomeAba);

                sheet.setColumnWidth(0, 3500);  // Data
                sheet.setColumnWidth(1, 4000);  // NF
                sheet.setColumnWidth(2, 5000);  // Valor Merc
                sheet.setColumnWidth(3, 7000);  // Destino
                sheet.setColumnWidth(4, 4000);  // Cidade
                sheet.setColumnWidth(5, 4000);  // Frete

                XSSFCellStyle estiloTitulo = criarEstilo(workbook, new byte[]{(byte) 200, (byte) 200, (byte) 200}, true, 11);
                XSSFCellStyle estiloHeader = criarEstilo(workbook, new byte[]{(byte) 180, (byte) 180, (byte) 180}, true, 10);
                XSSFCellStyle estiloLinha = criarEstilo(workbook, null, false, 10);
                XSSFCellStyle estiloTotal = criarEstilo(workbook, new byte[]{(byte) 220, (byte) 220, (byte) 220}, true, 10);

                int row = 0;

                // cabeçalho com nome do cliente
                Row cabecalho = sheet.createRow(row++);
                cabecalho.setHeightInPoints(20);
                Cell cellNome = cabecalho.createCell(0);
                cellNome.setCellValue(cliente.cliente());
                cellNome.setCellStyle(estiloTitulo);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

                // período
                Row periodoRow = sheet.createRow(row++);
                String periodo = dataInicio != null && dataFim != null
                        ? "Período: " + dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " a " + dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "Período: Todos";
                Cell cellPeriodo = periodoRow.createCell(0);
                cellPeriodo.setCellValue(periodo);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

                sheet.createRow(row++); // linha em branco

                // títulos das colunas
                Row tituloRow = sheet.createRow(row++);
                String[] titulos = {"Data", "Notas Fiscais", "Valor Merc.", "Destino", "Cidade", "Frete Valor"};
                for (int i = 0; i < titulos.length; i++) {
                    Cell c = tituloRow.createCell(i);
                    c.setCellValue(titulos[i]);
                    c.setCellStyle(estiloHeader);
                }

                // busca notas do cliente no período
                double totalValor = 0;
                double totalFrete = 0;

                // aqui precisamos das notas individuais do cliente
                // vamos usar uma query específica
                List<Object[]> notas = faturamentoService.buscarNotasDoCliente(
                        cliente.clienteId(), dataInicio, dataFim);

                for (Object[] nota : notas) {
                    Row linhaRow = sheet.createRow(row++);
                    linhaRow.setHeightInPoints(16);

                    String dataStr = "";
                    if (nota[0] != null) {
                        if (nota[0] instanceof java.sql.Timestamp) {
                            dataStr = ((java.sql.Timestamp) nota[0]).toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } else if (nota[0] instanceof LocalDateTime) {
                            dataStr = ((LocalDateTime) nota[0])
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } else {
                            dataStr = nota[0].toString(); // Fallback caso venha outro tipo
                        }
                    }

                    String numero = nota[1] != null ? (String) nota[1] : "";
                    Double valor = nota[2] != null ? ((Number) nota[2]).doubleValue() : 0.0;
                    String destinatario = nota[3] != null ? (String) nota[3] : "";
                    String cidade = nota[4] != null ? (String) nota[4] : "";
                    Double frete = nota[5] != null ? ((Number) nota[5]).doubleValue() : 0.0;

                    criarCelula(linhaRow, 0, dataStr, estiloLinha);
                    criarCelula(linhaRow, 1, numero, estiloLinha);
                    criarCelulaNumero(linhaRow, 2, valor, estiloLinha, workbook);
                    criarCelula(linhaRow, 3, destinatario, estiloLinha);
                    criarCelula(linhaRow, 4, cidade, estiloLinha);
                    criarCelulaNumero(linhaRow, 5, frete, estiloLinha, workbook);

                    totalValor += valor;
                    totalFrete += frete;
                }

                // linha de total
                sheet.createRow(row++);
                Row totalRow = sheet.createRow(row++);
                criarCelula(totalRow, 0, "TOTAL", estiloTotal);
                criarCelula(totalRow, 1, "", estiloTotal);
                criarCelulaNumero(totalRow, 2, totalValor, estiloTotal, workbook);
                criarCelula(totalRow, 3, "", estiloTotal);
                criarCelula(totalRow, 4, "", estiloTotal);
                criarCelulaNumero(totalRow, 5, totalFrete, estiloTotal, workbook);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de faturamento", e);
        }
    }

    private void criarCelula(Row row, int col, String valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private void criarCelulaNumero(Row row, int col, Double valor, CellStyle estiloBase, XSSFWorkbook workbook) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : 0.0);
        XSSFCellStyle estilo = workbook.createCellStyle();
        estilo.cloneStyleFrom(estiloBase);
        DataFormat format = workbook.createDataFormat();
        estilo.setDataFormat(format.getFormat("R$ #,##0.00"));
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