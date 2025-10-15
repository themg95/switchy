package folk.sisby.switchy.modules;

import dev.ashhhleyyy.playerpronouns.api.Pronouns;
import dev.ashhhleyyy.playerpronouns.api.PronounsApi;
import eu.pb4.placeholders.api.TextParserUtils;
import folk.sisby.switchy.SwitchyCompat;
import folk.sisby.switchy.api.module.SwitchyModule;
import folk.sisby.switchy.api.module.SwitchyModuleEditable;
import folk.sisby.switchy.api.module.SwitchyModuleInfo;
import folk.sisby.switchy.api.module.SwitchyModuleRegistry;
import folk.sisby.switchy.api.module.SwitchyModuleTransferable;
import folk.sisby.switchy.util.Feedback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static folk.sisby.switchy.util.Feedback.translatable;

/**
 * A module that switches pronouns from Ash's Player Pronouns.
 *
 * @author Esen
 * @see SwitchyModule
 */
public class PlayerPronounsModule implements SwitchyModule, SwitchyModuleTransferable {
	/**
	 * Identifier for this module.
	 */
	public static final Identifier ID = Feedback.identifier("switchy", "player_pronouns");

	/**
	 * The NBT key where the pronouns are stored.
	 */
	public static final String KEY_PRONOUNS = "pronouns";

	/**
	 * The pronouns object.
	 */
	@Nullable
	public Pronouns pronouns;

	/**
	 * Registers the module
	 */
	public static void register() {
		SwitchyModuleRegistry.registerModule(ID,PlayerPronounsModule::new, new SwitchyModuleInfo(
				true,
				SwitchyModuleEditable.ALWAYS_ALLOWED,
				translatable("switchy.modules.switchy.player_pronouns.description")
			)
				.withDescriptionWhenEnabled(translatable("switchy.modules.switchy.player_pronouns.enabled"))
				.withDescriptionWhenDisabled(translatable("switchy.modules.switchy.player_pronouns.disabled"))
				.withDeletionWarning(translatable("switchy.modules.switchy.player_pronouns.warning"))
		);
	}

	@Override
	public void updateFromPlayer(ServerPlayerEntity player, @Nullable String nextPreset) {
		pronouns = PronounsApi.getReader().getPronouns(player);
	}

	@Override
	public void applyToPlayer(ServerPlayerEntity player) {
		Pronouns oldPronouns = PronounsApi.getReader().getPronouns(player);
		if (pronouns != null) PronounsApi.getSetter().setPronouns(player, pronouns);
		Pronouns newPronouns = pronouns;
		if (!Objects.equals(oldPronouns, newPronouns))
			SwitchyCompat.LOGGER.info("[Switchy Compat] Player Pronouns Change: '{}' -> '{}' [{}]", oldPronouns, newPronouns, player.getGameProfile().getName());
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound outNbt = new NbtCompound();
		if (pronouns != null) outNbt.putString(KEY_PRONOUNS, pronouns.raw());
		return outNbt;
	}

	@Override
	public NbtCompound toClientNbt() {
		NbtCompound outNbt = new NbtCompound();
		if (pronouns != null) {
			outNbt.put(KEY_PRONOUNS, TextCodecs.CODEC.encodeStart(NbtOps.INSTANCE, getText()).getOrThrow());
		}
		return outNbt;
	}

	@Override
	public void fillFromNbt(NbtCompound nbt) {
		pronouns = nbt.contains(KEY_PRONOUNS) ? Pronouns.fromString(nbt.getString(KEY_PRONOUNS).orElse("")) : null;
	}

	/**
	 * @return a text representation of the stored pronouns.
	 */
	public Text getText() {
		return pronouns != null ? TextParserUtils.formatText(pronouns.raw()) : null;
	}
}
