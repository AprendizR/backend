package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.mapper.CargaMapper;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.dto.response.CargaDTODetalhada;
import com.erp.transportadora.dto.response.NotaFiscalDTOResumo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RomaneioExcelService {

    private final CargaRepository cargaRepository;

    @Transactional
    public byte[] gerarRomaneio(Long cargaId) {
        CargaEntity cargaEntity = cargaRepository.buscarComNotas(cargaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carga não encontrada"));

        CargaDTODetalhada carga = CargaMapper.toDetalhada(cargaEntity);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Romaneio");

            // larguras das colunas
            sheet.setColumnWidth(0, 1500);   // nº
            sheet.setColumnWidth(1, 3000);   // NF
            sheet.setColumnWidth(2, 3000);   // DATA NF
            sheet.setColumnWidth(3, 8000);   // LOCAL
            sheet.setColumnWidth(4, 8000);   // BAIRRO/CIDADE
            sheet.setColumnWidth(5, 6000);   // CLIENTE
            sheet.setColumnWidth(6, 5000);   // OBS MOTORISTA
            sheet.setColumnWidth(7, 3000);   // OS COLETA

            // estilos
            XSSFCellStyle estiloAviso = criarEstilo(workbook, new byte[]{(byte) 255, 0, 0}, IndexedColors.WHITE.getIndex(), true, 11);
            XSSFCellStyle estiloCabecalho = criarEstilo(workbook, new byte[]{(byte) 200, (byte) 200, (byte) 200}, IndexedColors.BLACK.getIndex(), true, 10);
            XSSFCellStyle estiloLabel = criarEstilo(workbook, new byte[]{(byte) 220, (byte) 220, (byte) 220}, IndexedColors.BLACK.getIndex(), true, 10);
            XSSFCellStyle estiloValor = criarEstilo(workbook, null, IndexedColors.BLACK.getIndex(), false, 10);
            XSSFCellStyle estiloTituloColuna = criarEstilo(workbook, new byte[]{(byte) 180, (byte) 180, (byte) 180}, IndexedColors.BLACK.getIndex(), true, 10);
            XSSFCellStyle estiloLinha = criarEstilo(workbook, null, IndexedColors.BLACK.getIndex(), false, 10);

            int row = 0;

            // linha de aviso
            Row aviso = sheet.createRow(row++);
            aviso.setHeightInPoints(18);
            Cell celulaAviso = aviso.createCell(0);
            celulaAviso.setCellValue("VERIFICAR TODOS OS DIAS AGUA E OLEO DO CARRO - VERIFICAR TODOS OS DIAS AGUA E OLEO DO CARRO");
            celulaAviso.setCellStyle(estiloAviso);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // linha em branco
            sheet.createRow(row++);

            // cabeçalho romaneio
            String dataFormatada = carga.dataCriacao() != null
                    ? carga.dataCriacao().format(DateTimeFormatter.ofPattern("dd/MM")) : "";

            criarLinhaCabecalho(sheet, row++, estiloLabel, estiloValor,
                    "ROMANEIO:", String.valueOf(carga.numeroRota()),
                    "DATA:", dataFormatada);

            criarLinhaCabecalho(sheet, row++, estiloLabel, estiloValor,
                    "CARRO:", carga.veiculo().modelo() + " " + carga.veiculo().placa(),
                    "KM SAIDA:", "");

            String nomeMotorista = carga.motorista().apelido() != null && !carga.motorista().apelido().isBlank()
                    ? carga.motorista().apelido() : carga.motorista().nome();
            String nomeAjudante = carga.ajudante() != null
                    ? (carga.ajudante().apelido() != null && !carga.ajudante().apelido().isBlank()
                    ? carga.ajudante().apelido() : carga.ajudante().nome())
                    : "";

            criarLinhaCabecalho(sheet, row++, estiloLabel, estiloValor,
                    "MOTORISTA:", nomeMotorista,
                    "AJUDANTE:", nomeAjudante);

            criarLinhaCabecalho(sheet, row++, estiloLabel, estiloValor,
                    "HORARIO:", "",
                    "HORARIO:", "");

            // linha em branco
            sheet.createRow(row++);

            // títulos das colunas
            Row tituloRow = sheet.createRow(row++);
            String[] titulos = {"", "NF", "DATA NF", "LOCAL", "BAIRRO / CIDADE", "CLIENTE", "OBS DO MOTORISTA", "OS COLETA"};
            for (int i = 0; i < titulos.length; i++) {
                Cell c = tituloRow.createCell(i);
                c.setCellValue(titulos[i]);
                c.setCellStyle(estiloTituloColuna);
            }

            // notas
            List<NotaFiscalDTOResumo> notas = carga.notasFiscais().stream()
                    .sorted((a, b) -> {
                        if (a.ordemEntrega() == null) return 1;
                        if (b.ordemEntrega() == null) return -1;
                        return a.ordemEntrega().compareTo(b.ordemEntrega());
                    })
                    .toList();
            for (int i = 0; i < 25; i++) {
                Row linhaRow = sheet.createRow(row++);
                linhaRow.setHeightInPoints(16);

                Cell numCell = linhaRow.createCell(0);
                numCell.setCellStyle(estiloLinha);

                if (i < notas.size()) {
                    NotaFiscalDTOResumo nota = notas.get(i);
                    numCell.setCellValue(i + 1);
                    criarCelula(linhaRow, 1, nota.numero() != null ? nota.numero() : "", estiloLinha);
                    criarCelula(linhaRow, 2, "", estiloLinha); // data NF em branco por enquanto
                    criarCelula(linhaRow, 3, nota.destinatario() != null ? nota.destinatario() : "", estiloLinha);
                    criarCelula(linhaRow, 4, nota.cidade() != null ? nota.cidade() : "", estiloLinha);
                    criarCelula(linhaRow, 5, nota.remetente() != null ? nota.remetente() : "", estiloLinha);
                    criarCelula(linhaRow, 6, "", estiloLinha); // obs motorista em branco
                    criarCelula(linhaRow, 7, String.valueOf(nota.ordemServico()), estiloLinha);
                } else {
                    for (int j = 1; j <= 7; j++) criarCelula(linhaRow, j, "", estiloLinha);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar romaneio", e);
        }
    }

    private void criarLinhaCabecalho(Sheet sheet, int rowNum, CellStyle estiloLabel, CellStyle estiloValor,
                                     String label1, String valor1, String label2, String valor2) {
        Row row = sheet.createRow(rowNum);
        row.setHeightInPoints(16);
        criarCelula(row, 0, label1, estiloLabel);
        criarCelula(row, 1, valor1, estiloValor);
        criarCelula(row, 2, "", estiloValor);
        criarCelula(row, 3, "", estiloValor);
        criarCelula(row, 4, label2, estiloLabel);
        criarCelula(row, 5, valor2, estiloValor);
        criarCelula(row, 6, "", estiloValor);
        criarCelula(row, 7, "", estiloValor);
    }

    private void criarCelula(Row row, int col, String valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private XSSFCellStyle criarEstilo(XSSFWorkbook workbook, byte[] corFundo, short corTexto, boolean negrito, int fontSize) {
        XSSFCellStyle estilo = workbook.createCellStyle();

        if (corFundo != null) {
            XSSFColor cor = new XSSFColor(corFundo, null);
            estilo.setFillForegroundColor(cor);
            estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        XSSFFont fonte = workbook.createFont();
        fonte.setColor(corTexto);
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