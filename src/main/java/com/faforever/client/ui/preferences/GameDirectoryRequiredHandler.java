package com.faforever.client.ui.preferences;

import com.faforever.client.i18n.I18n;
import com.faforever.client.ui.StageHolder;
import com.faforever.client.ui.preferences.event.GameDirectoryChooseEvent;
import com.faforever.client.ui.preferences.event.GameDirectoryChosenEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static javafx.application.Platform.runLater;


@Component
@RequiredArgsConstructor
public class GameDirectoryRequiredHandler implements InitializingBean {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final EventBus eventBus;
  private final I18n i18n;
  private CompletableFuture<Path> future;

  @Override
  public void afterPropertiesSet() {
    eventBus.register(this);
  }

  @Subscribe
  public void onChooseGameDirectory(GameDirectoryChooseEvent event) {
    runLater(() -> {
      final String baseGameName = event.getBaseGameName();
      Path path;
      {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(i18n.get("missingGamePath.chooserTitle", baseGameName.toUpperCase()));
        File result = fileChooser.showOpenDialog(StageHolder.getStage().getScene().getWindow());

        logger.info("User selected game path: {}", result);
        path = Optional.ofNullable(result).map(File::toPath).orElse(null);
      }

      final String[] commandLineOptions = new String[1];
      if (path != null)
      {
        TextInputDialog cmdLineOptionsInputDialog = new TextInputDialog("");
        cmdLineOptionsInputDialog.setTitle(String.format("Total Annihilation: %s", baseGameName.toUpperCase()));
        cmdLineOptionsInputDialog.setHeaderText(
            String.format("Executable for %s: %s\n\n", baseGameName.toUpperCase(), path.toString()) +
                i18n.get("settings.fa.executableDecorator.description"));
        cmdLineOptionsInputDialog.setContentText(i18n.get("settings.fa.executableDecorator"));

        Optional<String> result = cmdLineOptionsInputDialog.showAndWait();
        result.ifPresent(options -> { commandLineOptions[0] = options; });
      }

      eventBus.post(new GameDirectoryChosenEvent(path, commandLineOptions[0], event.getFuture(), event.getBaseGameName()));

    });
  }

}
