package at.hannibal2.skyhanni.config.features.mining;

import at.hannibal2.skyhanni.config.FeatureToggle;
import at.hannibal2.skyhanni.config.commands.Commands;
import at.hannibal2.skyhanni.config.core.config.Position;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigLink;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class MiningFortuneConfig {
    @Expose
    @ConfigOption(
        name = "MF Display",
        desc = "Display the true Mining Fortune for the current ore, including all ore-specific and hidden bonuses."
    )
    @ConfigEditorBoolean
    @FeatureToggle
    public boolean display = false;

    @ConfigOption(name = "Mining Fortune Guide", desc = "Open a guide that breaks down your Mining Fortune.\n§eCommand: /mf")
    @ConfigEditorButton(buttonText = "Open")
    public Runnable open = Commands::openMiningGuide;

    @Expose
    @ConfigLink(owner = MiningFortuneConfig.class, field = "display")
    public Position pos = new Position(5, -180, false, true);
}
