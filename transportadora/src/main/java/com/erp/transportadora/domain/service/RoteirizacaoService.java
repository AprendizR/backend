package com.erp.transportadora.domain.service;

import com.erp.transportadora.domain.entity.CargaEntity;
import com.erp.transportadora.domain.entity.NotaFiscalEntity;
import com.erp.transportadora.domain.repository.CargaRepository;
import com.erp.transportadora.domain.repository.NotaFiscalRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoteirizacaoService {

    private final CargaRepository cargaRepository;
    private final NotaFiscalRepository notaFiscalRepository;

    @Value("${transportadora.latitude:-23.4754321}")
    private Double origemLat;

    @Value("${transportadora.longitude:-46.5356067}")
    private Double origemLng;

    @Transactional
    public void roteirizar(Long cargaId) {
        CargaEntity carga = cargaRepository.buscarComNotas(cargaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carga não encontrada"));

        List<NotaFiscalEntity> notas = carga.getNotasFiscais().stream()
                .filter(n -> n.getLatitude() != null || n.getCidade() != null)
                .toList();

        if (notas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma nota com coordenadas ou cidade encontrada");
        }

        // preenche coordenadas das notas que só têm cidade
        List<NotaFiscalEntity> notasOrdenadas = new ArrayList<>();
        for (NotaFiscalEntity nota : notas) {
            if (nota.getLatitude() == null && nota.getCidade() != null) {
                double[] coords = buscarCoordsPorCidade(nota.getCidade());
                if (coords != null) {
                    nota.setLatitude(coords[0]);
                    nota.setLongitude(coords[1]);
                    notaFiscalRepository.save(nota);
                }
            }
            if (nota.getLatitude() != null) notasOrdenadas.add(nota);
        }

        if (notasOrdenadas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível obter coordenadas para nenhuma nota");
        }

        // monta URL do OSRM
        StringBuilder coords = new StringBuilder();
        coords.append(origemLng).append(",").append(origemLat);
        for (NotaFiscalEntity nota : notasOrdenadas) {
            coords.append(";").append(nota.getLongitude()).append(",").append(nota.getLatitude());
        }

        String url = "https://router.project-osrm.org/trip/v1/driving/" + coords
                + "?source=first&destination=any&roundtrip=false";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao chamar OSRM");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode waypoints = root.get("waypoints");

            if (waypoints == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta inválida do OSRM");
            }

            for (int i = 1; i < waypoints.size(); i++) {
                int waypointIndex = waypoints.get(i).get("waypoint_index").asInt();
                NotaFiscalEntity nota = notasOrdenadas.get(i - 1);
                nota.setOrdemEntrega(waypointIndex + 1);
                notaFiscalRepository.save(nota);
                System.out.println("Nota OS=" + nota.getOrdemServico() + " ordemEntrega=" + nota.getOrdemEntrega());
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao roteirizar", e);
        }
    }

    private double[] buscarCoordsPorCidade(String cidade) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + java.net.URLEncoder.encode(cidade + ", SP, Brasil", "UTF-8")
                    + "&format=json&limit=1";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "OrtizLog/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            if (root.isArray() && root.size() > 0) {
                double lat = root.get(0).get("lat").asDouble();
                double lng = root.get(0).get("lon").asDouble();
                return new double[]{lat, lng};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}