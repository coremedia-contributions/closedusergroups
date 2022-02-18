import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CUGStudioPlugin_properties from "./CUGStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "CUGStudioPlugin" for Locale "de".
 * @see CUGStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(CUGStudioPlugin_properties, {
  closedusergroup: "Statusgruppe(n)",
  MultiListSelector_left: "Verfügbare Gruppen",
  MultiListSelector_left_emptyText: "Alle verfügbaren Gruppen wurden bereits zugewiesen.",
  MultiListSelector_right: "Ausgewählte Gruppen",
  MultiListSelector_right_emptyText: "Sichtbarkeit durch Zuweisung von Gruppen einschränken.",
});
