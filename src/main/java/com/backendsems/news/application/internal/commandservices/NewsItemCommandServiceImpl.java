package com.backendsems.news.application.internal.commandservices;

import com.backendsems.news.domain.model.aggregates.NewsItem;
import com.backendsems.news.domain.model.commands.CreateNewsItemCommand;
import com.backendsems.news.domain.model.commands.SeedNewsItemsCommand;
import com.backendsems.news.domain.services.NewsItemCommandService;
import com.backendsems.news.infrastructure.repositories.jpa.NewsItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsItemCommandServiceImpl implements NewsItemCommandService {

    private final NewsItemRepository newsItemRepository;

    public NewsItemCommandServiceImpl(NewsItemRepository newsItemRepository) {
        this.newsItemRepository = newsItemRepository;
    }

    @Override
    public Optional<NewsItem> handle(CreateNewsItemCommand command) {
        NewsItem newsItem = new NewsItem(command.title(), command.content(), command.isTip(),
                command.summary(), command.imageUrl(), command.category(), command.link());
        newsItemRepository.save(newsItem);
        return Optional.of(newsItem);
    }

    @Override
    public void handle(SeedNewsItemsCommand command) {
        if (newsItemRepository.count() > 0) {
            return;
        }

        List<NewsItem> standardNewsItems = List.of(
                new NewsItem(
                        "Bienvenido al nuevo panel de Noticias y Consejos",
                        "A partir de hoy podrás encontrar aquí novedades de la plataforma y consejos prácticos para reducir el consumo eléctrico de tu hogar.",
                        false,
                        "Estrenamos una sección para mantenerte informado sobre SEMS.",
                        null, "Novedades", null
                ),
                new NewsItem(
                        "Reduce tu consumo en horas punta",
                        "Programar el uso de electrodomésticos como la lavadora o el horno fuera del horario punta puede reducir significativamente el costo de tu recibo mensual.",
                        true,
                        "Evita usar tus equipos de mayor consumo entre las 18:00 y 23:00 horas.",
                        null, "Ahorro de energía", null
                ),
                new NewsItem(
                        "Cambia a iluminación LED y ahorra hasta 80%",
                        "Reemplazar los focos incandescentes o fluorescentes por LED reduce el consumo de iluminación hasta en un 80% y tienen una vida útil mucho más larga.",
                        true,
                        "Un cambio simple con impacto inmediato en tu factura.",
                        null, "Eficiencia energética", null
                ),
                new NewsItem(
                        "Elimina el consumo fantasma de tus dispositivos",
                        "Muchos equipos siguen consumiendo energía en modo espera. Desconectarlos o usar regletas con interruptor puede generar ahorros mensuales notables.",
                        true,
                        "Revisa qué dispositivos dejas conectados en espera todo el día.",
                        null, "Mantenimiento", null
                ),
                new NewsItem(
                        "Nuevo plan Anual disponible con mayores beneficios",
                        "Lanzamos el plan Anual, pensado para quienes buscan el máximo ahorro y acceso anticipado a nuevas funcionalidades de la plataforma.",
                        false,
                        "Conoce los beneficios del plan Anual en la sección de Planes.",
                        null, "Planes", null
                ),
                new NewsItem(
                        "Cuida tu aire acondicionado este verano",
                        "Subir la temperatura del aire acondicionado unos pocos grados y mantener limpios los filtros ayuda a reducir el consumo sin perder confort.",
                        true,
                        "Cada grado adicional puede representar un ahorro considerable.",
                        null, "Ahorro de energía", null
                )
        );

        newsItemRepository.saveAll(standardNewsItems);
    }
}
