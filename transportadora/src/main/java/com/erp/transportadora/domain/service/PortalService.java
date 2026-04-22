package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PortalService {

    private final ClienteRepository clienteRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    public Map<String, Object> login(String cnpj, String senha) {
        String cnpjLimpo = cnpj.replaceAll("\\D", "");
        ClienteEntity cliente = clienteRepository.findByCnpj(cnpjLimpo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CNPJ não encontrado"));

        String senhaPortal = cliente.getSenhaPortal() != null ? cliente.getSenhaPortal() : "123";
        if (!senha.equals(senhaPortal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cnpj", cnpjLimpo);
        response.put("nome", cliente.getNome());
        return response;
    }

    public List<Map<String, Object>> buscarNotas(String cnpj, String numero, String status, String dataInicio, String dataFim) {
        String cnpjLimpo = cnpj.replaceAll("\\D", "");
        ClienteEntity cliente = clienteRepository.findByCnpj(cnpjLimpo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        List<NotaFiscalEntity> notas = notaFiscalRepository.findByClienteId(cliente.getId());

        // filtro por número
        if (numero != null && !numero.isBlank()) {
            notas = notas.stream()
                    .filter(n -> n.getNumero() != null && n.getNumero().contains(numero))
                    .toList();
        }

        // filtro por status
        if (status != null && !status.isBlank()) {
            notas = notas.stream()
                    .filter(n -> n.getStatus() != null && n.getStatus().name().equals(status))
                    .toList();
        }

        // filtro por data
        if (dataInicio != null && !dataInicio.isBlank()) {
            LocalDateTime inicio = LocalDate.parse(dataInicio).atStartOfDay();
            notas = notas.stream()
                    .filter(n -> n.getDataEmissao() != null && !n.getDataEmissao().isBefore(inicio))
                    .toList();
        }
        if (dataFim != null && !dataFim.isBlank()) {
            LocalDateTime fim = LocalDate.parse(dataFim).atTime(23, 59, 59);
            notas = notas.stream()
                    .filter(n -> n.getDataEmissao() != null && !n.getDataEmissao().isAfter(fim))
                    .toList();
        }

        // se não pesquisou nada, retorna últimas 10 entregues
        if ((numero == null || numero.isBlank()) && (status == null || status.isBlank())
                && (dataInicio == null || dataInicio.isBlank()) && (dataFim == null || dataFim.isBlank())) {
            notas = notas.stream()
                    .filter(n -> n.getStatus() != null && n.getStatus().name().equals("ENTREGUE"))
                    .sorted(Comparator.comparing(NotaFiscalEntity::getDataEmissao, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(10)
                    .toList();
        }

        return notas.stream().map(this::toPortalDTO).toList();
    }

    private Map<String, Object> toPortalDTO(NotaFiscalEntity nota) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ordemServico", nota.getOrdemServico());
        map.put("numero", nota.getNumero());
        map.put("status", nota.getStatus() != null ? nota.getStatus().name() : null);
        map.put("destinatario", nota.getDestinatario());
        map.put("cidade", nota.getCidade());
        map.put("dataEmissao", nota.getDataEmissao() != null
                ? nota.getDataEmissao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);

        if (nota.getCarga() != null) {
            map.put("veiculo", nota.getCarga().getVeiculo() != null
                    ? nota.getCarga().getVeiculo().getPlaca() + " - " + nota.getCarga().getVeiculo().getModelo() : null);
            map.put("motorista", nota.getCarga().getMotorista() != null
                    ? (nota.getCarga().getMotorista().getApelido() != null
                    ? nota.getCarga().getMotorista().getApelido()
                    : nota.getCarga().getMotorista().getNome()) : null);
        } else {
            map.put("veiculo", null);
            map.put("motorista", null);
        }

        return map;
    }
}