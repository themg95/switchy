package folk.sisby.switchy.client.modules;

import com.mojang.datafixers.util.Pair;
import folk.sisby.switchy.client.api.module.SwitchyClientModule;
import folk.sisby.switchy.client.api.module.SwitchyClientModuleRegistry;
import folk.sisby.switchy.modules.PlayerPronounsModule;
import folk.sisby.switchy.ui.api.SwitchyUIPosition;
import folk.sisby.switchy.ui.api.module.SwitchyUIModule;
import folk.sisby.switchy.util.Feedback;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * The client-displayable variant of a module that switches switches pronouns from Ash's Player Pronouns.
 *
 * @author Sisby folk
 * @see SwitchyUIModule
 * @see PlayerPronounsModule
 * @since 2.0.0
 */
public class PlayerPronounsClientModule implements SwitchyClientModule, SwitchyUIModule {
	/**
	 * Identifier for this module.
	 * Must match {@link PlayerPronounsModule}.
	 */
	public static final Identifier ID = Feedback.identifier("switchy", "player_pronouns");
	/**
	 * The NBT key where the pronouns (in serialized text format) is stored.
	 * Must match {@link PlayerPronounsModule#toClientNbt()}.
	 */
	public static final String KEY_PRONOUNS = "pronouns";
	/**
	 * The styled pronouns, in Text format.
	 */
	public @Nullable Text pronouns;

	/**
	 * Registers the module
	 */
	public static void register() {
		SwitchyClientModuleRegistry.registerModule(ID, PlayerPronounsClientModule::new);
	}

	@Override
	public Pair<Component, SwitchyUIPosition> getPreviewComponent(String presetName) {
		if (pronouns == null) return null;
		return Pair.of(Components.label(pronouns), SwitchyUIPosition.LEFT);
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound outNbt = new NbtCompound();
		if (pronouns != null) {
			outNbt.put(KEY_PRONOUNS, TextCodecs.CODEC.encodeStart(NbtOps.INSTANCE, pronouns).getOrThrow());
		}
		return outNbt;
	}

	@Override
	public void fillFromNbt(NbtCompound nbt) {
		if (nbt.contains(KEY_PRONOUNS)) pronouns = TextCodecs.CODEC.decode(NbtOps.INSTANCE, nbt.get(KEY_PRONOUNS)).getOrThrow().getFirst();
	}
}
