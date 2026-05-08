package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.entity.OcorrenciaEntity;
import com.erp.transportadora.domain.enums.StatusNota;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.erp.transportadora.domain.repository.OcorrenciaRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioNotaService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final OcorrenciaRepository ocorrenciaRepository;

    @Transactional
    public byte[] gerarRelatorio(Long notaId) {
        NotaFiscalEntity nota = notaFiscalRepository.findById(notaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada"));

        if (nota.getStatus() == StatusNota.EM_ROTA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relatório disponível apenas para notas entregues");
        }

        List<OcorrenciaEntity> ocorrencias = ocorrenciaRepository.findByNotaFiscalOrdemServico(nota.getOrdemServico());
        OcorrenciaEntity ultimaOcorrencia = ocorrencias.stream()
                .max(Comparator.comparing(OcorrenciaEntity::getDataOcorrencia))
                .orElse(null);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("COMPROVANTE DE ENTREGA")
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(20));

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            addRow(table, "Nº da Nota Fiscal:", nota.getNumero() != null ? nota.getNumero() : "—");
            addRow(table, "Cliente:", nota.getCliente() != null ? nota.getCliente().getNome() : "—");
            addRow(table, "Destinatário:", nota.getDestinatario() != null ? nota.getDestinatario() : "—");
            addRow(table, "Cidade:", nota.getCidade() != null ? nota.getCidade() : "—");

            if (nota.getCarga() != null) {
                String motorista = nota.getCarga().getMotorista() != null
                        ? (nota.getCarga().getMotorista().getApelido() != null && !nota.getCarga().getMotorista().getApelido().isBlank()
                        ? nota.getCarga().getMotorista().getApelido()
                        : nota.getCarga().getMotorista().getNome())
                        : "—";
                String veiculo = nota.getCarga().getVeiculo() != null
                        ? nota.getCarga().getVeiculo().getModelo() + " " + nota.getCarga().getVeiculo().getPlaca()
                        : "—";
                addRow(table, "Motorista:", motorista);
                addRow(table, "Veículo:", veiculo);
            }

            if (ultimaOcorrencia != null) {
                addRow(table, "Ocorrência:", formatOcorrencia(ultimaOcorrencia.getSubtipo().name()));
                addRow(table, "Recebedor:", ultimaOcorrencia.getNomeRecebedor() != null ? ultimaOcorrencia.getNomeRecebedor() : "");
                addRow(table, "Data:", ultimaOcorrencia.getDataOcorrencia()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                if (ultimaOcorrencia.getObservacao() != null && !ultimaOcorrencia.getObservacao().isBlank()) {
                    addRow(table, "Observação:", ultimaOcorrencia.getObservacao());
                }
            }

            document.add(table);

            if (!nota.getFotos().isEmpty()) {
                boolean temImagem = nota.getFotos().stream().anyMatch(f -> !f.endsWith(".pdf"));
                if (temImagem) {
                    document.add(new Paragraph("COMPROVANTES FOTOGRÁFICOS")
                            .setBold()
                            .setFontSize(12)
                            .setMarginTop(20)
                            .setMarginBottom(10));

                    for (String caminho : nota.getFotos()) {
                        if (caminho.endsWith(".pdf")) continue;
                        Path fotoPath = Paths.get("uploads").resolve(caminho);
                        if (!Files.exists(fotoPath)) continue;
                        try {
                            byte[] fotoBytes = Files.readAllBytes(fotoPath);
                            Image img = new Image(ImageDataFactory.create(fotoBytes))
                                    .setWidth(UnitValue.createPercentValue(100))
                                    .setMarginBottom(10);
                            document.add(img);
                        } catch (Exception ignored) {}
                    }
                }
            }

            document.close();
            byte[] relatorio = out.toByteArray();

            List<String> fotosPdf = nota.getFotos().stream()
                    .filter(f -> f.endsWith(".pdf"))
                    .filter(f -> Files.exists(Paths.get("uploads").resolve(f)))
                    .toList();

            if (fotosPdf.isEmpty()) return relatorio;

            ByteArrayOutputStream mergedOut = new ByteArrayOutputStream();
            try (PdfDocument mergedPdf = new PdfDocument(new PdfWriter(mergedOut))) {
                PdfDocument relatorioPdf = new PdfDocument(
                        new com.itextpdf.kernel.pdf.PdfReader(
                                new java.io.ByteArrayInputStream(relatorio)
                        )
                );
                relatorioPdf.copyPagesTo(1, relatorioPdf.getNumberOfPages(), mergedPdf);
                relatorioPdf.close();

                for (String caminho : fotosPdf) {
                    Path fotoPath = Paths.get("uploads").resolve(caminho);
                    PdfDocument fotoPdf = new PdfDocument(
                            new com.itextpdf.kernel.pdf.PdfReader(fotoPath.toFile())
                    );
                    fotoPdf.copyPagesTo(1, fotoPdf.getNumberOfPages(), mergedPdf);
                    fotoPdf.close();
                }
            }
            return mergedOut.toByteArray();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar relatório", e);
        }
    }

    private void addRow(Table table, String label, String valor) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell()
                .add(new Paragraph(valor)));
    }

    private String formatOcorrencia(String subtipo) {
        return switch (subtipo) {
            case "ENTREGA_COMPLETA" -> "Entrega Completa";
            case "ENTREGA_PARCIAL" -> "Entrega Parcial";
            case "ENTREGA_RECUSADA" -> "Entrega Recusada";
            case "COLETA_COMPLETA" -> "Coleta Completa";
            case "COLETA_PARCIAL" -> "Coleta Parcial";
            case "COLETA_NAO_EFETUADA" -> "Coleta Não Efetuada";
            case "TROCA_COMPLETA" -> "Troca Completa";
            case "TROCA_PARCIAL" -> "Troca Parcial";
            case "TROCA_NAO_EFETUADA" -> "Troca Não Efetuada";
            case "DESTINATARIO_AUSENTE" -> "Destinatário Ausente";
            case "ENDERECO_INCORRETO" -> "Endereço Incorreto";
            case "CANCELADA_PELO_CLIENTE" -> "Cancelada pelo Cliente";
            case "CANCELADA_OPERACIONAL" -> "Cancelada Operacional";
            default -> subtipo;
        };
    }
}