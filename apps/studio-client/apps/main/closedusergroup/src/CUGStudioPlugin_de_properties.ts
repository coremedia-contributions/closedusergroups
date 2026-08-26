import CUGStudioPlugin_properties from "./CUGStudioPlugin_properties";

Object.assign(CUGStudioPlugin_properties, {
  closedusergroup: "Statusgruppe(n)",
  MultiListSelector_left: "Verfügbare Gruppen",
  MultiListSelector_left_emptyText: "Alle verfügbaren Gruppen wurden bereits zugewiesen.",
  MultiListSelector_right: "Ausgewählte Gruppen",
  MultiListSelector_right_emptyText: "Sichtbarkeit durch Zuweisung von Gruppen einschränken.",

  GatedContentForm_title: "Zugriffsgeschützte Inhalte",
  "CMTeasable_localSettings.gatedContent.ctaText_emptyText": "Fügen Sie hier den CTA-Text ein.",
  "CMTeasable_localSettings.gatedContent.ctaText_text": "CTA Text",
  "CMTeasable_localSettings.gatedContent.ctaText_tooltip": "CTA-Text für zugangsbeschränkte Inhalte überschreiben",
  "CMTeasable_localSettings.gatedContent.enabled_text": "Zugriffsgeschützte Inhalte aktiviert",
  "CMTeasable_localSettings.gatedContent.enabled_tooltip": "",
  "CMTeasable_localSettings.gatedContent.enabled_true_text": "Der Zugriff auf den Inhalt erfolgt über ein Formular.",
  "CMTeasable_localSettings.gatedContent.form_empyText": "Formular hier einfügen.",
  "CMTeasable_localSettings.gatedContent.form_text": "Formular für zugangsbeschränkte Inhalte",
  "CMTeasable_localSettings.gatedContent.form_tooltip": "Formular für zugangsbeschränkte Inhalte",
} satisfies Partial<CUGStudioPlugin_properties>);
