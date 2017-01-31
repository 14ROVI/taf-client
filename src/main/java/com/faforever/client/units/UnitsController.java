package com.faforever.client.units;

import com.faforever.client.fx.AbstractViewController;
import com.google.common.base.Strings;
import javafx.scene.Node;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UnitsController extends AbstractViewController<Node> {

  private final String unitDatabaseUrl;
  public WebView unitsRoot;

  @Inject
  public UnitsController(@Value("${unitDatabase.url}") String unitDatabaseUrl) {
    this.unitDatabaseUrl = unitDatabaseUrl;
  }

  @Override
  public void onDisplay() {
    if (Strings.isNullOrEmpty(unitsRoot.getEngine().getLocation())) {
      unitsRoot.getEngine().load(unitDatabaseUrl);
    }
  }

  public Node getRoot() {
    return unitsRoot;
  }
}
