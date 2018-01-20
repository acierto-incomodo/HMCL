/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2017  huangyuhui <huanghongxun2008@126.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.ui.download;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jackhuang.hmcl.Main;
import org.jackhuang.hmcl.download.DownloadProvider;
import org.jackhuang.hmcl.game.GameRepository;
import org.jackhuang.hmcl.ui.FXUtils;
import org.jackhuang.hmcl.ui.construct.Validator;
import org.jackhuang.hmcl.ui.wizard.WizardController;
import org.jackhuang.hmcl.ui.wizard.WizardPage;
import org.jackhuang.hmcl.util.StringUtils;

import java.util.Map;

public class InstallersPage extends StackPane implements WizardPage {
    private final WizardController controller;
    private final GameRepository repository;
    private final DownloadProvider downloadProvider;

    @FXML
    private VBox list;

    @FXML
    private JFXButton btnForge;

    @FXML
    private JFXButton btnLiteLoader;

    @FXML
    private JFXButton btnOptiFine;

    @FXML
    private Label lblGameVersion;

    @FXML
    private Label lblForge;

    @FXML
    private Label lblLiteLoader;

    @FXML
    private Label lblOptiFine;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXButton btnInstall;

    public InstallersPage(WizardController controller, GameRepository repository, DownloadProvider downloadProvider) {
        this.controller = controller;
        this.repository = repository;
        this.downloadProvider = downloadProvider;

        FXUtils.loadFXML(this, "/assets/fxml/download/installers.fxml");

        String gameVersion = (String) controller.getSettings().get("game");
        Validator hasVersion = new Validator(s -> !repository.hasVersion(s) && StringUtils.isNotBlank(s));
        hasVersion.setMessage(Main.i18n("install.new_game.already_exists"));
        txtName.getValidators().add(hasVersion);
        txtName.textProperty().addListener(e -> btnInstall.setDisable(!txtName.validate()));
        txtName.setText(gameVersion);

        btnForge.setOnMouseClicked(e -> {
            controller.getSettings().put(INSTALLER_TYPE, 0);
            controller.onNext(new VersionsPage(controller, Main.i18n("install.installer.choose", Main.i18n("install.installer.forge")), gameVersion, downloadProvider, "forge", () -> controller.onPrev(false)));
        });

        btnLiteLoader.setOnMouseClicked(e -> {
            controller.getSettings().put(INSTALLER_TYPE, 1);
            controller.onNext(new VersionsPage(controller, Main.i18n("install.installer.choose", Main.i18n("install.installer.liteloader")), gameVersion, downloadProvider, "liteloader", () -> controller.onPrev(false)));
        });

        btnOptiFine.setOnMouseClicked(e -> {
            controller.getSettings().put(INSTALLER_TYPE, 2);
            controller.onNext(new VersionsPage(controller, Main.i18n("install.installer.choose", Main.i18n("install.installer.optifine")), gameVersion, downloadProvider, "optifine", () -> controller.onPrev(false)));
        });
    }

    @Override
    public String getTitle() {
        return Main.i18n("install.new_game");
    }

    @Override
    public void onNavigate(Map<String, Object> settings) {
        lblGameVersion.setText(Main.i18n("install.new_game.current_game_version") + ": " + controller.getSettings().get("game"));
        if (controller.getSettings().containsKey("forge"))
            lblForge.setText(Main.i18n("install.installer.version", Main.i18n("install.installer.forge")) + ": " + controller.getSettings().get("forge"));
        else
            lblForge.setText(Main.i18n("install.installer.not_installed", Main.i18n("install.installer.forge")));

        if (controller.getSettings().containsKey("liteloader"))
            lblLiteLoader.setText(Main.i18n("install.installer.version", Main.i18n("install.installer.liteloader")) + ": " + controller.getSettings().get("liteloader"));
        else
            lblLiteLoader.setText(Main.i18n("install.installer.not_installed", Main.i18n("install.installer.liteloader")));

        if (controller.getSettings().containsKey("optifine"))
            lblOptiFine.setText(Main.i18n("install.installer.version", Main.i18n("install.installer.optifine")) + ": " + controller.getSettings().get("optifine"));
        else
            lblOptiFine.setText(Main.i18n("install.installer.not_installed", Main.i18n("install.installer.optifine")));
    }

    @Override
    public void cleanup(Map<String, Object> settings) {
        settings.remove(INSTALLER_TYPE);
    }

    public void onInstall() {
        controller.getSettings().put("name", txtName.getText());
        controller.onFinish();
    }

    public static final String INSTALLER_TYPE = "INSTALLER_TYPE";
}
