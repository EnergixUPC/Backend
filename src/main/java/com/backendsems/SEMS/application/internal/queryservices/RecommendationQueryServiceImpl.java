package com.backendsems.SEMS.application.internal.queryservices;

import com.backendsems.SEMS.domain.model.aggregates.Device;
import com.backendsems.SEMS.domain.model.aggregates.UserSetting;
import com.backendsems.SEMS.domain.model.entities.Consumption;
import com.backendsems.SEMS.domain.model.entities.DeviceConsumption;
import com.backendsems.SEMS.domain.model.queries.GetTopDevicesByUserQuery;
import com.backendsems.SEMS.domain.model.valueobjects.UserId;
import com.backendsems.SEMS.domain.services.DeviceQueryService;
import com.backendsems.SEMS.domain.services.RecommendationQueryService;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import com.backendsems.SEMS.infrastructure.persistence.jpa.repositories.SettingsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RecommendationQueryServiceImpl
 * Ver {@link RecommendationQueryService}.
 */
@Service
public class RecommendationQueryServiceImpl implements RecommendationQueryService {

    private static final Map<String, String> CATEGORY_TIPS = Map.ofEntries(
            Map.entry("refrigeracion", "Revisa el sellado de la puerta y evita abrirla con frecuencia; un refrigerador ineficiente puede duplicar su consumo."),
            Map.entry("iluminacion", "Cambia a bombillas LED de bajo consumo y aprovecha la luz natural durante el dia."),
            Map.entry("climatizacion", "Ajusta el termostato 1-2 grados y usa un temporizador para evitar que quede encendido sin necesidad."),
            Map.entry("calefaccion", "Ajusta el termostato 1-2 grados y usa un temporizador para evitar que quede encendido sin necesidad."),
            Map.entry("entretenimiento", "Desconecta este dispositivo del modo espera cuando no lo uses; el consumo fantasma se acumula."),
            Map.entry("cocina", "Usalo en horarios fuera de punta y evita precalentar mas tiempo del necesario."),
            Map.entry("lavado", "Programa el lavado en horario fuera de punta y con carga completa para aprovechar mejor cada ciclo.")
    );
    private static final String DEFAULT_TIP = "Revisa cuanto tiempo permanece encendido este dispositivo; reducir su uso diario es la forma mas directa de bajar tu consumo.";
    private static final double PEAK_HOUR_SHARE_THRESHOLD = 0.4;

    private final DeviceQueryService deviceQueryService;
    private final ConsumptionRepository consumptionRepository;
    private final SettingsRepository settingsRepository;

    public RecommendationQueryServiceImpl(DeviceQueryService deviceQueryService,
                                           ConsumptionRepository consumptionRepository,
                                           SettingsRepository settingsRepository) {
        this.deviceQueryService = deviceQueryService;
        this.consumptionRepository = consumptionRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public List<String> generateLegacyRecommendations(UserId userId) {
        var topDevices = deviceQueryService.handle(new GetTopDevicesByUserQuery(userId, 10, "monthly"));

        String leastEfficientDevice = topDevices.isEmpty() ? null
                : topDevices.get(0).getDevice().getName().name();

        return leastEfficientDevice != null
                ? List.of(
                    String.format("%s es tu dispositivo de mayor consumo este periodo; revisa cuanto tiempo permanece encendido.", leastEfficientDevice),
                    "Usa bombillas LED de bajo consumo.",
                    "Desconecta los electrodomesticos en modo espera.")
                : List.of(
                    "Aun no hay suficientes datos de consumo para generar recomendaciones personalizadas.",
                    "Usa bombillas LED de bajo consumo.",
                    "Desconecta los electrodomesticos en modo espera.");
    }

    @Override
    public List<String> generatePersonalizedRecommendations(UserId userId) {
        var topDevices = deviceQueryService.handle(new GetTopDevicesByUserQuery(userId, 3, "monthly"));
        if (topDevices.isEmpty()) {
            return List.of("Aun no hay suficientes datos de consumo para generar recomendaciones personalizadas.");
        }

        List<String> recommendations = new ArrayList<>();
        for (DeviceConsumption topDevice : topDevices) {
            Device device = topDevice.getDevice();
            String tip = resolveTip(device.getCategory().category());
            recommendations.add(String.format("%s (%s): %s", device.getName().name(), device.getCategory().category(), tip));
        }

        appendPeakHourTip(userId, topDevices.get(0).getDevice(), recommendations);
        return recommendations;
    }

    private void appendPeakHourTip(UserId userId, Device topDevice, List<String> recommendations) {
        var settingsOpt = settingsRepository.findByUserId(userId);
        if (settingsOpt.isEmpty() || settingsOpt.get().getPeakHourStart() == null) {
            return;
        }
        UserSetting settings = settingsOpt.get();

        List<Consumption> deviceConsumptions = consumptionRepository.findByDeviceIdInAndCalculatedAtBetween(
                List.of(String.valueOf(topDevice.getId())),
                LocalDateTime.now().minusDays(30), LocalDateTime.now());
        if (deviceConsumptions.isEmpty()) {
            return;
        }

        double total = deviceConsumptions.stream().mapToDouble(Consumption::getConsumption).sum();
        double peak = deviceConsumptions.stream()
                .filter(c -> settings.isWithinPeakHour(c.getCalculatedAt().toLocalTime()))
                .mapToDouble(Consumption::getConsumption).sum();

        if (total > 0 && (peak / total) > PEAK_HOUR_SHARE_THRESHOLD) {
            recommendations.add(String.format(
                    "%.0f%% del consumo de %s ocurre en tu horario pico (%s-%s); considera moverlo a otro horario.",
                    (peak / total) * 100.0, topDevice.getName().name(),
                    settings.getPeakHourStart(), settings.getPeakHourEnd()));
        }
    }

    /**
     * Las categorías son texto libre creado por cada usuario (ver DeviceCategory), así que se
     * busca el tip por coincidencia parcial en vez de igualdad exacta.
     */
    private String resolveTip(String rawCategory) {
        String normalized = rawCategory.toLowerCase()
                .replace("ó", "o").replace("í", "i").replace("é", "e")
                .replace("á", "a").replace("ú", "u");
        return CATEGORY_TIPS.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_TIP);
    }
}
