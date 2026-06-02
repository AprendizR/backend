package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.ClienteEntity;
import com.erp.transportadora.domain.entity.FreteClienteEntity;
import com.erp.transportadora.domain.enums.TipoFrete;
import com.erp.transportadora.domain.repository.ClienteRepository;
import com.erp.transportadora.domain.repository.FreteClienteRepository;
import com.erp.transportadora.dto.request.FreteClienteDTORequest;
import com.erp.transportadora.dto.response.FreteClienteDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreteClienteService {

    private final FreteClienteRepository freteRepository;
    private final ClienteRepository clienteRepository;

    public List<FreteClienteDTOResponse> listarPorCliente(Long clienteId) {
        return freteRepository.findByClienteId(clienteId).stream().map(this::toResponse).toList();
    }

    public Optional<FreteClienteDTOResponse> buscar(Long clienteId, String cidade, Double valorNota) {
        return buscarRegra(clienteId, cidade).map(frete -> toResponse(frete, valorNota));
    }

    public Optional<FreteClienteEntity> buscarRegra(Long clienteId, String cidade) {
        Optional<FreteClienteEntity> porCidade = freteRepository.findByClienteIdAndCidadeIgnoreCase(clienteId, cidade);
        if (porCidade.isPresent()) return porCidade;
        return freteRepository.findByClienteIdAndCidadeIgnoreCase(clienteId, "TODAS");
    }

    public Optional<Double> calcularFrete(Long clienteId, String cidade, Double valorNota) {
        return buscarRegra(clienteId, cidade).map(frete -> calcularValor(frete, valorNota));
    }

    public FreteClienteDTOResponse salvar(FreteClienteDTORequest dto) {
        ClienteEntity cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        String cidade = normalizarCidade(dto.cidade());
        FreteClienteEntity frete = freteRepository.findByClienteIdAndCidadeIgnoreCase(dto.clienteId(), cidade)
                .orElse(FreteClienteEntity.builder().cliente(cliente).build());

        TipoFrete tipo = parseTipo(dto.tipo());
        Double valorCalculado = tipo == TipoFrete.CIDADE || tipo == TipoFrete.FRETE_FIXO
                ? dto.valor()
                : calcularPorPercentual(dto.percentual(), dto.adicional(), null);

        frete.setCidade(cidade);
        frete.setTipo(tipo);
        frete.setPercentual(dto.percentual());
        frete.setAdicional(dto.adicional());
        frete.setValor(valorCalculado != null ? valorCalculado : 0.0);
        return toResponse(freteRepository.save(frete));
    }

    public void deletar(Long id) {
        if (!freteRepository.existsById(id)) throw new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Frete não encontrado");
        freteRepository.deleteById(id);
    }

    private FreteClienteDTOResponse toResponse(FreteClienteEntity e) {
        return toResponse(e, null);
    }

    private FreteClienteDTOResponse toResponse(FreteClienteEntity e, Double valorNota) {
        return new FreteClienteDTOResponse(
                e.getId(),
                e.getCliente().getId(),
                e.getCidade(),
                calcularValor(e, valorNota),
                e.getTipo() != null ? e.getTipo().name() : TipoFrete.CIDADE.name(),
                e.getPercentual(),
                e.getAdicional()
        );
    }

    private Double calcularValor(FreteClienteEntity frete, Double valorNota) {
        TipoFrete tipo = frete.getTipo() != null ? frete.getTipo() : TipoFrete.CIDADE;
        if (tipo == TipoFrete.PERCENTUAL || tipo == TipoFrete.HIBRIDO) {
            return calcularPorPercentual(frete.getPercentual(), frete.getAdicional(), valorNota);
        }
        return frete.getValor();
    }

    private Double calcularPorPercentual(Double percentual, Double adicional, Double valorNota) {
        if (valorNota == null || percentual == null) return 0.0;
        return (valorNota * percentual / 100.0) + (adicional != null ? adicional : 0.0);
    }

    private TipoFrete parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) return TipoFrete.CIDADE;
        return TipoFrete.valueOf(tipo);
    }

    private String normalizarCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) return "TODAS";
        return cidade.toUpperCase().trim();
    }
}
