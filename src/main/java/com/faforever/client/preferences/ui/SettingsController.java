package com.faforever.client.preferences.ui;

import com.faforever.client.chat.ChatColorMode;
import com.faforever.client.chat.ChatFormat;
import com.faforever.client.config.ClientProperties;
import com.faforever.client.fx.Controller;
import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.fx.PlatformService;
import com.faforever.client.fx.StringCell;
import com.faforever.client.fx.StringListCell;
import com.faforever.client.game.KnownFeaturedMod;
import com.faforever.client.i18n.I18n;
import com.faforever.client.mod.FeaturedMod;
import com.faforever.client.mod.ModService;
import com.faforever.client.notification.Action;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.notification.PersistentNotification;
import com.faforever.client.notification.Severity;
import com.faforever.client.notification.TransientNotification;
import com.faforever.client.preferences.AutoUploadLogsOption;
import com.faforever.client.preferences.DateInfo;
import com.faforever.client.preferences.LocalizationPrefs;
import com.faforever.client.preferences.MaxPacketSizeOption;
import com.faforever.client.preferences.NotificationsPrefs;
import com.faforever.client.preferences.Preferences;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.preferences.TadaIntegrationOption;
import com.faforever.client.preferences.AskAlwaysOrNever;
import com.faforever.client.preferences.TimeInfo;
import com.faforever.client.preferences.ToastPosition;
import com.faforever.client.preferences.TotalAnnihilationPrefs;
import com.faforever.client.settings.LanguageItemController;
import com.faforever.client.theme.Theme;
import com.faforever.client.theme.UiService;
import com.faforever.client.ui.list.NoSelectionModel;
import com.faforever.client.ui.preferences.event.GameDirectoryChooseEvent;
import com.faforever.client.update.ClientUpdateService;
import com.faforever.client.user.UserService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.faforever.client.fx.JavaFxUtil.PATH_STRING_CONVERTER;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SettingsController implements Controller<Node> {

  private final NotificationService notificationService;
  private final UserService userService;
  private final PreferencesService preferencesService;
  private final UiService uiService;
  private final I18n i18n;
  private final EventBus eventBus;
  private final PlatformService platformService;
  private final ClientProperties clientProperties;
  private final ClientUpdateService clientUpdateService;
  private final ModService modService;

  public TextField executableDecoratorField;
  public TextField executionDirectoryField;
  public ToggleGroup colorModeToggleGroup;
  public Toggle randomColorsToggle;
  public Toggle defaultColorsToggle;
  public CheckBox hideFoeToggle;
  public CheckBox forceRelayToggle;
  public TextField iceAcceptableLatencyTextField;
  public CheckBox proactiveResendToggle;
  public ComboBox<MaxPacketSizeOption> maxPacketSizeOptionComboBox;
  public CheckBox suppressReplayChatToggle;
  public CheckBox enableIrcIntegrationToggle;
  public CheckBox enableAutoLaunchOnHostToggle;
  public CheckBox enableAutoLaunchOnJoinToggle;
  public CheckBox enableAutoRehostToggle;
  public CheckBox enableAutoTeamBalanceToggle;
  public CheckBox enableSequencedLaunchToggle;
  public CheckBox enableAutoJoinToggle;
  public CheckBox requireUacToggle;
  public TableView<TotalAnnihilationPrefs> gameLocationTableView;
  public TableColumn<TotalAnnihilationPrefs, String> gameLocationModTableColumn;
  public TableColumn<TotalAnnihilationPrefs, String> gameLocationExecutableTableColumn;
  public TableColumn<TotalAnnihilationPrefs, String> gameLocationCommandLineOptionsTableColumn;
  public CheckBox autoDownloadMapsToggle;
  public TextField maxMessagesTextField;
  public CheckBox imagePreviewToggle;
  public CheckBox enableNotificationsToggle;
  public CheckBox enableSoundsToggle;
  public CheckBox displayFriendOnlineToastCheckBox;
  public CheckBox displayFriendOfflineToastCheckBox;
  public CheckBox playFriendOnlineSoundCheckBox;
  public CheckBox playFriendOfflineSoundCheckBox;
  public CheckBox displayFriendJoinsGameToastCheckBox;
  public CheckBox displayPlayerJoinsGameToastCheckBox;
  public CheckBox displayFriendPlaysGameToastCheckBox;
  public CheckBox playFriendJoinsGameSoundCheckBox;
  public CheckBox playPlayerJoinsGameSoundCheckBox;
  public CheckBox playFriendPlaysGameSoundCheckBox;
  public CheckBox displayPmReceivedToastCheckBox;
  public CheckBox playPmReceivedSoundCheckBox;
  public CheckBox afterGameReviewCheckBox;
  public Region settingsRoot;
  public ComboBox<Theme> themeComboBox;
  public ToggleGroup toastPositionToggleGroup;
  public ComboBox<Screen> toastScreenComboBox;
  public Toggle bottomLeftToastButton;
  public Toggle topRightToastButton;
  public Toggle topLeftToastButton;
  public ToggleButton bottomRightToastButton;
  public ComboBox<TimeInfo> timeComboBox;
  public ComboBox<DateInfo> dateComboBox;
  public ComboBox<ChatFormat> chatComboBox;
  public Label passwordChangeErrorLabel;
    public CheckBox notifyOnAtMentionOnlyToggle;
  public Pane languagesContainer;
  public TextField backgroundImageLocation;
  public CheckBox disallowJoinsCheckBox;
  public CheckBox advancedIceLogToggle;
  public CheckBox prereleaseToggle;
  public Region settingsHeader;
  public ComboBox<TadaIntegrationOption> tadaIntegrationComboBox;
  public ComboBox<AskAlwaysOrNever> featuredModRevertOptionComboBox;
  public ComboBox<AutoUploadLogsOption> autoUploadLogsOptionComboBox;
  public Label notifyAtMentionTitle;
  public Label notifyAtMentionDescription;
  public TextField channelTextField;
  public Button addChannelButton;
  public ListView<String> autoChannelListView;
  public Button clearCacheButton;
  public CheckBox gameDataCacheCheckBox;
  public CheckBox gameDataMapManagementCheckBox;
  public CheckBox gameDataPromptDownloadCheckBox;
  public CheckBox gameDataMapDownloadKeepVersionTagCheckBox;
  public Spinner<Integer> gameDataCacheTimeSpinner;
  public CheckBox allowReplayWhileInGameCheckBox;
  public Button allowReplayWhileInGameButton;
  public CheckBox debugLogToggle;

  private final InvalidationListener availableLanguagesListener;

  private ChangeListener<Theme> selectedThemeChangeListener;
  private ChangeListener<Theme> currentThemeChangeListener;

  public SettingsController(UserService userService, PreferencesService preferencesService, UiService uiService,
                            I18n i18n, EventBus eventBus, NotificationService notificationService,
                            PlatformService platformService, ClientProperties clientProperties,
                            ClientUpdateService clientUpdateService, ModService modService) {
    this.userService = userService;
    this.preferencesService = preferencesService;
    this.uiService = uiService;
    this.i18n = i18n;
    this.eventBus = eventBus;
    this.notificationService = notificationService;
    this.platformService = platformService;
    this.clientProperties = clientProperties;
    this.clientUpdateService = clientUpdateService;
    this.modService = modService;

    availableLanguagesListener = observable -> {
      LocalizationPrefs localization = preferencesService.getPreferences().getLocalization();
      Locale currentLocale = localization.getLanguage();
      List<Node> nodes = i18n.getAvailableLanguages().stream()
          .map(locale -> {
            LanguageItemController controller = uiService.loadFxml("theme/settings/language_item.fxml");
            controller.setLocale(locale);
            controller.setOnSelectedListener(this::onLanguageSelected);
            controller.setSelected(locale.equals(currentLocale));
            return controller.getRoot();
          })
          .collect(Collectors.toList());
      languagesContainer.getChildren().setAll(nodes);
    };
  }

  /**
   * Disables preferences that should not be enabled since they are not supported yet.
   */
  private void temporarilyDisableUnsupportedSettings(Preferences preferences) {
    NotificationsPrefs notification = preferences.getNotification();
    notification.setFriendOnlineSoundEnabled(false);
    notification.setFriendOfflineSoundEnabled(false);
    notification.setFriendOfflineSoundEnabled(false);
    notification.setFriendPlaysGameSoundEnabled(false);
    notification.setFriendPlaysGameToastEnabled(false);
  }

  private void setSelectedToastPosition(ToastPosition newValue) {
    switch (newValue) {
      case TOP_RIGHT:
        toastPositionToggleGroup.selectToggle(topRightToastButton);
        break;
      case BOTTOM_RIGHT:
        toastPositionToggleGroup.selectToggle(bottomRightToastButton);
        break;
      case BOTTOM_LEFT:
        toastPositionToggleGroup.selectToggle(bottomLeftToastButton);
        break;
      case TOP_LEFT:
        toastPositionToggleGroup.selectToggle(topLeftToastButton);
        break;
    }
  }

  public void initialize() {
    eventBus.register(this);
    themeComboBox.setButtonCell(new StringListCell<>(Theme::getDisplayName));
    themeComboBox.setCellFactory(param -> new StringListCell<>(Theme::getDisplayName));

    toastScreenComboBox.setButtonCell(screenListCell());
    toastScreenComboBox.setCellFactory(param -> screenListCell());
    toastScreenComboBox.setItems(Screen.getScreens());
    NumberFormat integerNumberFormat = NumberFormat.getIntegerInstance();
    integerNumberFormat.setGroupingUsed(false);
    NumberStringConverter numberToStringConverter = new NumberStringConverter(integerNumberFormat);

    Preferences preferences = preferencesService.getPreferences();
    temporarilyDisableUnsupportedSettings(preferences);

    JavaFxUtil.bindBidirectional(maxMessagesTextField.textProperty(), preferences.getChat().maxMessagesProperty(), numberToStringConverter);
    imagePreviewToggle.selectedProperty().bindBidirectional(preferences.getChat().previewImageUrlsProperty());
    enableNotificationsToggle.selectedProperty().bindBidirectional(preferences.getNotification().transientNotificationsEnabledProperty());

    hideFoeToggle.selectedProperty().bindBidirectional(preferences.getChat().hideFoeMessagesProperty());

    disallowJoinsCheckBox.selectedProperty().bindBidirectional(preferences.disallowJoinsViaDiscordProperty());

    JavaFxUtil.addListener(preferences.getChat().chatColorModeProperty(), (observable, oldValue, newValue) -> setSelectedColorMode(newValue));
    setSelectedColorMode(preferences.getChat().getChatColorMode());

    colorModeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == defaultColorsToggle) {
        preferences.getChat().setChatColorMode(ChatColorMode.DEFAULT);
      }
      if (newValue == randomColorsToggle) {
        preferences.getChat().setChatColorMode(ChatColorMode.RANDOM);
      }
    });

    currentThemeChangeListener = (observable, oldValue, newValue) -> themeComboBox.getSelectionModel().select(newValue);
    selectedThemeChangeListener = (observable, oldValue, newValue) -> {
      uiService.setTheme(newValue);
      if (oldValue != null && uiService.doesThemeNeedRestart(newValue)) {
        notificationService.addNotification(new PersistentNotification(i18n.get("theme.needsRestart.message", newValue.getDisplayName()), Severity.WARN,
            Collections.singletonList(new Action(i18n.get("theme.needsRestart.quit"), event -> Platform.exit()))));
        // FIXME reload application (stage & application context) https://github.com/FAForever/downlords-faf-client/issues/1794
      }
    };

    JavaFxUtil.addListener(preferences.getNotification().toastPositionProperty(), (observable, oldValue, newValue) -> setSelectedToastPosition(newValue));
    setSelectedToastPosition(preferences.getNotification().getToastPosition());
    toastPositionToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == topLeftToastButton) {
        preferences.getNotification().setToastPosition(ToastPosition.TOP_LEFT);
      }
      if (newValue == topRightToastButton) {
        preferences.getNotification().setToastPosition(ToastPosition.TOP_RIGHT);
      }
      if (newValue == bottomLeftToastButton) {
        preferences.getNotification().setToastPosition(ToastPosition.BOTTOM_LEFT);
      }
      if (newValue == bottomRightToastButton) {
        preferences.getNotification().setToastPosition(ToastPosition.BOTTOM_RIGHT);
      }
    });
    configureTimeSetting(preferences);
    configureDateSetting(preferences);
    configureChatSetting(preferences);
    configureLanguageSelection();
    configureThemeSelection();
    configureToastScreen(preferences);
    configureTadaIntegration(preferences);
    configureFeaturedModRevertOption(preferences);
    configureAutoUploadLogs(preferences);
    configureMaxPacketSizeOption(preferences);

    displayFriendOnlineToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendOnlineToastEnabledProperty());
    displayFriendOfflineToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendOfflineToastEnabledProperty());
    displayFriendJoinsGameToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendJoinsGameToastEnabledProperty());
    displayPlayerJoinsGameToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().playerJoinsGameToastEnabledProperty());
    displayFriendPlaysGameToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendPlaysGameToastEnabledProperty());
    displayPmReceivedToastCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().privateMessageToastEnabledProperty());
    playFriendOnlineSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendOnlineSoundEnabledProperty());
    playFriendOfflineSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendOfflineSoundEnabledProperty());
    playFriendJoinsGameSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendJoinsGameSoundEnabledProperty());
    playPlayerJoinsGameSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().playerJoinsGameSoundEnabledProperty());
    playFriendPlaysGameSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().friendPlaysGameSoundEnabledProperty());
    playPmReceivedSoundCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().privateMessageSoundEnabledProperty());
    afterGameReviewCheckBox.selectedProperty().bindBidirectional(preferences.getNotification().afterGameReviewEnabledProperty());

    notifyOnAtMentionOnlyToggle.selectedProperty().bindBidirectional(preferences.getNotification().notifyOnAtMentionOnlyEnabledProperty());
    enableSoundsToggle.selectedProperty().bindBidirectional(preferences.getNotification().soundsEnabledProperty());
    gameLocationModTableColumn.setCellValueFactory(new PropertyValueFactory<>("getModName"));
    gameLocationModTableColumn.setCellFactory(param -> new StringCell<>(modService::getFeaturedModDisplayName));
    gameLocationExecutableTableColumn.setCellValueFactory(new PropertyValueFactory<>("getInstalledExePath"));
    gameLocationCommandLineOptionsTableColumn.setCellValueFactory(new PropertyValueFactory<>("getCommandLineOptions"));

    // ensure SettingsController knows about all the mods that ModService knows about
    modService.getFeaturedMods()
        .thenApply(modList -> {
          modList.stream()
            .filter(FeaturedMod::isVisible)
            .forEach(featuredMod -> preferencesService.getTotalAnnihilation(featuredMod.getTechnicalName()));
          return modList;
        });

//    gameLocationExecutableTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//    gameLocationExecutableTableColumn.setOnEditCommit(
//        t -> t.getTableView()
//            .getItems()
//            .get(t.getTablePosition().getRow())
//            .setInstalledPath(Paths.get(t.getNewValue())));
//
//    gameLocationCommandLineOptionsTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//    gameLocationCommandLineOptionsTableColumn.setOnEditCommit(
//       t -> t.getTableView()
//            .getItems()
//            .get(t.getTablePosition().getRow())
//            .setCommandLineOptions(t.getNewValue()));

    forceRelayToggle.selectedProperty().bindBidirectional(preferences.getForceRelayEnabledProperty());
    proactiveResendToggle.selectedProperty().bindBidirectional(preferences.getProactiveResendEnabledProperty());
    suppressReplayChatToggle.selectedProperty().bindBidirectional(preferences.getSuppressReplayChatEnabledProperty());
    enableIrcIntegrationToggle.selectedProperty().bindBidirectional(preferences.getIrcIntegrationEnabledProperty());
    enableAutoLaunchOnHostToggle.selectedProperty().bindBidirectional(preferences.getAutoLaunchOnHostEnabledProperty());
    enableAutoLaunchOnJoinToggle.selectedProperty().bindBidirectional(preferences.getAutoLaunchOnJoinEnabledProperty());
    enableAutoRehostToggle.selectedProperty().bindBidirectional(preferences.getAutoRehostEnabledProperty());
    enableAutoTeamBalanceToggle.selectedProperty().bindBidirectional(preferences.getAutoTeamBalanceEnabledProperty());
    enableSequencedLaunchToggle.selectedProperty().bindBidirectional(preferences.getSequencedLaunchProperty());
    enableAutoJoinToggle.selectedProperty().bindBidirectional(preferences.getAutoJoinEnabledProperty());
    requireUacToggle.selectedProperty().bindBidirectional(preferences.getRequireUacEnabledProperty());
    updateGameLocationTable();

    backgroundImageLocation.textProperty().bindBidirectional(preferences.getMainWindow().backgroundImagePathProperty(), PATH_STRING_CONVERTER);

    autoChannelListView.setSelectionModel(new NoSelectionModel<>());
    autoChannelListView.setFocusTraversable(false);
    autoChannelListView.setItems(preferencesService.getPreferences().getChat().getAutoJoinChannels());
    autoChannelListView.setCellFactory(param -> uiService.<RemovableListCellController>loadFxml("theme/settings/removable_cell.fxml"));
    autoChannelListView.getItems().addListener((ListChangeListener<String>) c -> preferencesService.storeInBackground());
    autoChannelListView.managedProperty().bind(autoChannelListView.visibleProperty());
    autoChannelListView.visibleProperty().bind(Bindings.createBooleanBinding(() -> !autoChannelListView.getItems().isEmpty(), autoChannelListView.getItems()));

    advancedIceLogToggle.selectedProperty().bindBidirectional(preferences.advancedIceLogEnabledProperty());

    prereleaseToggle.selectedProperty().bindBidirectional(preferences.preReleaseCheckEnabledProperty());
    prereleaseToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue && (oldValue == null || !oldValue)) {
        clientUpdateService.checkForUpdateInBackground();
      }
    });

    debugLogToggle.selectedProperty().bindBidirectional(preferences.debugLogEnabledProperty());
    initNotifyMeOnAtMention();
    initGameDataCache();
  }

  private void initGameDataCache() {
    gameDataCacheCheckBox.selectedProperty().bindBidirectional(preferencesService.getPreferences().gameDataCacheActivatedProperty());
    gameDataMapManagementCheckBox.selectedProperty().bindBidirectional(preferencesService.getPreferences().gameDataMapManagementEnabledProperty());
    gameDataPromptDownloadCheckBox.selectedProperty().bindBidirectional(preferencesService.getPreferences().gameDataPromptDownloadActivatedProperty());
    gameDataMapDownloadKeepVersionTagCheckBox.selectedProperty().bindBidirectional(preferencesService.getPreferences().gameDataMapDownloadKeepVersionTagProperty());
    //Binding for CacheLifeTimeInDays does not work because of some java fx bug
    gameDataCacheTimeSpinner.getValueFactory().setValue(preferencesService.getPreferences().getCacheLifeTimeInDays());
    gameDataCacheTimeSpinner.getValueFactory().valueProperty()
        .addListener((observable, oldValue, newValue) -> preferencesService.getPreferences().setCacheLifeTimeInDays(newValue));
  }

  private void initNotifyMeOnAtMention() {
    String username = userService.getUsername();
    notifyAtMentionTitle.setText(i18n.get("settings.chat.notifyOnAtMentionOnly", "@" + username));
    notifyAtMentionDescription.setText(i18n.get("settings.chat.notifyOnAtMentionOnly.description", "@" + username));
  }

  public void updateGameLocationTable()
  {
    gameLocationTableView.setItems(preferencesService.getTotalAnnihilationAllMods());
  }

  private void configureMaxPacketSizeOption(Preferences preferences) {
    maxPacketSizeOptionComboBox.setItems(FXCollections.observableArrayList(MaxPacketSizeOption.TINY, MaxPacketSizeOption.NORMAL));
    maxPacketSizeOptionComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(MaxPacketSizeOption option) {
        return i18n.get(option.getI18nKey());
      }

      @Override
      public MaxPacketSizeOption fromString(String s) {
        throw new UnsupportedOperationException("Not needed");
      }
    });
    maxPacketSizeOptionComboBox.setValue(preferences.getMaxPacketSizeOption());
    preferences.maxPacketSizeOptionProperty().bindBidirectional(maxPacketSizeOptionComboBox.valueProperty());
  }

  private void configureTadaIntegration(Preferences preferences) {
    tadaIntegrationComboBox.setItems(FXCollections.observableArrayList(TadaIntegrationOption.values()));
    tadaIntegrationComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(TadaIntegrationOption option) {
        return i18n.get(option.getI18nKey());
      }

      @Override
      public TadaIntegrationOption fromString(String s) {
        throw new UnsupportedOperationException("Not needed");
      }
    });
    tadaIntegrationComboBox.setValue(preferences.getTadaIntegrationOption());
    preferences.tadaIntegrationOptionProperty().bindBidirectional(tadaIntegrationComboBox.valueProperty());
  }

  private void configureFeaturedModRevertOption(Preferences preferences) {
    featuredModRevertOptionComboBox.setItems(FXCollections.observableArrayList(AskAlwaysOrNever.values()));
    featuredModRevertOptionComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(AskAlwaysOrNever option) {
        return i18n.get(option.getI18nKey());
      }

      @Override
      public AskAlwaysOrNever fromString(String s) {
        throw new UnsupportedOperationException("Not needed");
      }
    });
    featuredModRevertOptionComboBox.setValue(preferences.getFeaturedModRevertOption());
    preferences.featuredModRevertOptionProperty().bindBidirectional(featuredModRevertOptionComboBox.valueProperty());
  }

  public void onTadaIntegrationSelected(ActionEvent actionEvent) {
    this.preferencesService.getPreferences().tadaIntegrationOptionProperty().setValue(tadaIntegrationComboBox.getValue());
    preferencesService.storeInBackground();
  }

  private void configureTimeSetting(Preferences preferences) {
    timeComboBox.setButtonCell(new StringListCell<>(timeInfo -> i18n.get(timeInfo.getDisplayNameKey())));
    timeComboBox.setCellFactory(param -> new StringListCell<>(timeInfo -> i18n.get(timeInfo.getDisplayNameKey())));
    timeComboBox.setItems(FXCollections.observableArrayList(TimeInfo.values()));
    timeComboBox.setDisable(false);
    timeComboBox.setFocusTraversable(true);
    timeComboBox.getSelectionModel().select(preferences.getChat().getTimeFormat());
  }

  public void onTimeFormatSelected() {
    log.debug("A new time format was selected: {}", timeComboBox.getValue());
    Preferences preferences = preferencesService.getPreferences();
    preferences.getChat().setTimeFormat(timeComboBox.getValue());
    preferencesService.storeInBackground();
  }

  private void configureDateSetting(Preferences preferences) {
    dateComboBox.setButtonCell(new StringListCell<>(dateInfo -> i18n.get(dateInfo.getDisplayNameKey())));
    dateComboBox.setCellFactory(param -> new StringListCell<>(dateInfo -> i18n.get(dateInfo.getDisplayNameKey())));
    dateComboBox.setItems(FXCollections.observableArrayList(DateInfo.values()));
    dateComboBox.setDisable(false);
    dateComboBox.setFocusTraversable(true);
    dateComboBox.getSelectionModel().select(preferences.getChat().getDateFormat());
  }

  public void onDateFormatSelected() {
    log.debug("A new date format was selected: {}", dateComboBox.getValue());
    Preferences preferences = preferencesService.getPreferences();
    preferences.getChat().setDateFormat(dateComboBox.getValue());
    preferencesService.storeInBackground();
  }


  private void configureChatSetting(Preferences preferences) {
    chatComboBox.setButtonCell(new StringListCell<>(chatFormat -> i18n.get(chatFormat.getI18nKey())));
    chatComboBox.setCellFactory(param -> new StringListCell<>(chatFormat -> i18n.get(chatFormat.getI18nKey())));
    chatComboBox.setItems(FXCollections.observableArrayList(ChatFormat.values()));
    chatComboBox.getSelectionModel().select(preferences.getChat().getChatFormat());
  }

  public void onChatFormatSelected() {
    log.debug("A new chat format was selected: {}", chatComboBox.getValue());
    Preferences preferences = preferencesService.getPreferences();
    preferences.getChat().setChatFormat(chatComboBox.getValue());
    preferencesService.storeInBackground();
  }

  private StringListCell<Screen> screenListCell() {
    return new StringListCell<>(screen -> i18n.get("settings.screenFormat", Screen.getScreens().indexOf(screen) + 1));
  }

  private void setSelectedColorMode(ChatColorMode newValue) {
    if (newValue != null) {
      switch (newValue) {
        case DEFAULT -> colorModeToggleGroup.selectToggle(defaultColorsToggle);
        case RANDOM -> colorModeToggleGroup.selectToggle(randomColorsToggle);
      }
    } else {
      colorModeToggleGroup.selectToggle(defaultColorsToggle);
    }
  }

  private void configureThemeSelection() {
    themeComboBox.setItems(FXCollections.observableArrayList(uiService.getAvailableThemes()));

    themeComboBox.getSelectionModel().select(uiService.getCurrentTheme());

    themeComboBox.getSelectionModel().selectedItemProperty().addListener(selectedThemeChangeListener);
    JavaFxUtil.addListener(uiService.currentThemeProperty(), new WeakChangeListener<>(currentThemeChangeListener));
  }

  private void configureLanguageSelection() {
    i18n.getAvailableLanguages().addListener(new WeakInvalidationListener(availableLanguagesListener));
    availableLanguagesListener.invalidated(i18n.getAvailableLanguages());
  }

  @VisibleForTesting
  void onLanguageSelected(Locale locale) {
    LocalizationPrefs localizationPrefs = preferencesService.getPreferences().getLocalization();
    if (Objects.equals(locale, localizationPrefs.getLanguage())) {
      return;
    }
    log.debug("A new language was selected: {}", locale);
    localizationPrefs.setLanguage(locale);
    preferencesService.storeInBackground();

    availableLanguagesListener.invalidated(i18n.getAvailableLanguages());

    notificationService.addNotification(new PersistentNotification(
        i18n.get(locale, "settings.languages.restart.message"),
        Severity.WARN,
        Collections.singletonList(new Action(i18n.get(locale, "settings.languages.restart"),
            event -> {
              Platform.exit();
              // FIXME reload application (stage & application context)
            })
        )));
  }

  private void configureToastScreen(Preferences preferences) {
    toastScreenComboBox.getSelectionModel().select(preferences.getNotification().getToastScreen());
    preferences.getNotification().toastScreenProperty().bind(Bindings.createIntegerBinding(()
        -> Screen.getScreens().indexOf(toastScreenComboBox.getValue()), toastScreenComboBox.valueProperty()));
  }

  public Region getRoot() {
    return settingsRoot;
  }

  public void onSelectGameLocation() {
    int index = gameLocationTableView.getSelectionModel().selectedIndexProperty().get();
    final String modTechnical;
    if (gameLocationModTableColumn.getCellObservableValue(index) == null) {
      modTechnical = KnownFeaturedMod.DEFAULT.getTechnicalName();
    }
    else {
      modTechnical = ((SimpleStringProperty)gameLocationModTableColumn.getCellObservableValue(index)).get();
    }
    eventBus.post(new GameDirectoryChooseEvent(modTechnical));
  }

  public void onSelectExecutionDirectory() {
    // TODO implement
  }

  public void onPreviewToastButtonClicked() {
    notificationService.addNotification(new TransientNotification(
        i18n.get("settings.notifications.toastPreview.title"),
        i18n.get("settings.notifications.toastPreview.text")
    ));
  }

  public void onHelpUsButtonClicked() {
    platformService.showDocument(clientProperties.getTranslationProjectUrl());
  }

  public void onSelectBackgroundImage() {
    JavaFxUtil.runLater(() -> {
      FileChooser directoryChooser = new FileChooser();
      directoryChooser.setTitle(i18n.get("settings.appearance.chooseImage"));
      File result = directoryChooser.showOpenDialog(getRoot().getScene().getWindow());

      if (result == null) {
        return;
      }
      preferencesService.getPreferences().getMainWindow().setBackgroundImagePath(result.toPath());
      preferencesService.storeInBackground();
    });
  }

  public void onUseNoBackgroundImage(ActionEvent actionEvent) {
    preferencesService.getPreferences().getMainWindow().setBackgroundImagePath(null);
    preferencesService.storeInBackground();
  }

  public void openDiscordFeedbackChannel() {
    platformService.showDocument(clientProperties.getDiscord().getDiscordPrereleaseFeedbackChannelUrl());
  }

  public void openWebsite() {
    platformService.showDocument(clientProperties.getWebsite().getBaseUrl());
  }

  public void onAddAutoChannel() {
    if (channelTextField.getText().isEmpty() || autoChannelListView.getItems().contains(channelTextField.getText())) {
      return;
    }
    if (!channelTextField.getText().startsWith("#")) {
      channelTextField.setText("#" + channelTextField.getText());
    }
    preferencesService.getPreferences().getChat().getAutoJoinChannels().add(channelTextField.getText());
    preferencesService.storeInBackground();
    channelTextField.clear();
  }

  private void configureAutoUploadLogs(Preferences preferences) {
    autoUploadLogsOptionComboBox.setItems(FXCollections.observableArrayList(AutoUploadLogsOption.values()));
    autoUploadLogsOptionComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(AutoUploadLogsOption option) {
        return i18n.get(option.getI18nKey());
      }

      @Override
      public AutoUploadLogsOption fromString(String s) {
        throw new UnsupportedOperationException("Not needed");
      }
    });
    autoUploadLogsOptionComboBox.setValue(preferences.getAutoUploadLogsOption());
    preferences.autoUploadLogsOptionProperty().bindBidirectional(autoUploadLogsOptionComboBox.valueProperty());
  }

  public void onAutoUploadLogsSelected(ActionEvent actionEvent) {
    this.preferencesService.getPreferences().autoUploadLogsOptionProperty().setValue(autoUploadLogsOptionComboBox.getValue());
    preferencesService.storeInBackground();
  }
}

