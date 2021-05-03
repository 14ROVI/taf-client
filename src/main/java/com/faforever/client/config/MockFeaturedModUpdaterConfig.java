package com.faforever.client.config;

import com.faforever.client.FafClientApplication;
import com.faforever.client.mod.ModService;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.patch.FeaturedModUpdater;
import com.faforever.client.patch.GameUpdater;
import com.faforever.client.patch.GameUpdaterImpl;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.remote.FafService;
import com.faforever.client.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(FafClientApplication.PROFILE_OFFLINE)
@AllArgsConstructor
public class MockFeaturedModUpdaterConfig {

  private final ModService modService;
  private final ApplicationContext applicationContext;
  private final TaskService taskService;
  private final FafService fafService;
  private final FeaturedModUpdater featuredModUpdater;
  private final PreferencesService preferencesService;
  private final NotificationService notificationService;

  @Bean
  GameUpdater gameUpdater() {
    return new GameUpdaterImpl(modService, applicationContext, taskService, fafService, preferencesService, notificationService)
        .addFeaturedModUpdater(featuredModUpdater);
  }
}
