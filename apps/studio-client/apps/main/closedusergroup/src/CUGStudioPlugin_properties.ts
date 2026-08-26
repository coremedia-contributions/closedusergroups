
/**
 * Interface values for ResourceBundle "CUGStudioPlugin".
 * @see CUGStudioPlugin_properties#INSTANCE
 */
interface CUGStudioPlugin_properties {

  closedusergroup: string;
  cug_symbol_type: string;
  cug_groups_basepaths: string;
  MultiListSelector_left: string;
  MultiListSelector_left_emptyText: string;
  MultiListSelector_right: string;
  MultiListSelector_right_emptyText: string;

  /**
   * Gated Content
   */
  GatedContentForm_title: string;
  "CMTeasable_localSettings.gatedContent.enabled_text": string;
  "CMTeasable_localSettings.gatedContent.enabled_true_text": string;
  "CMTeasable_localSettings.gatedContent.enabled_tooltip": string;
  "CMTeasable_localSettings.gatedContent.form_text": string;
  "CMTeasable_localSettings.gatedContent.form_tooltip": string;
  "CMTeasable_localSettings.gatedContent.form_empyText": string;
  "CMTeasable_localSettings.gatedContent.ctaText_text": string;
  "CMTeasable_localSettings.gatedContent.ctaText_tooltip": string;
  "CMTeasable_localSettings.gatedContent.ctaText_emptyText": string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CUGStudioPlugin".
 * @see CUGStudioPlugin_properties
 */
const CUGStudioPlugin_properties: CUGStudioPlugin_properties = {
  closedusergroup: "Membership Group(s)",
  cug_symbol_type: "CMSymbol",
  cug_groups_basepaths: "\/Settings/Options/Groups/,Options/Groups/",
  MultiListSelector_left: "Available Groups",
  MultiListSelector_left_emptyText: "All groups have already been assigned.",
  MultiListSelector_right: "Assigned Groups",
  MultiListSelector_right_emptyText: "Assign groups to restrict the visibility of content items residing in this folder.",
  GatedContentForm_title: "Gated Content",
  "CMTeasable_localSettings.gatedContent.ctaText_emptyText": "Add CTA text here.",
  "CMTeasable_localSettings.gatedContent.ctaText_text": "CTA Text",
  "CMTeasable_localSettings.gatedContent.ctaText_tooltip": "Override default CTA text for gated content",
  "CMTeasable_localSettings.gatedContent.enabled_text": "Gated Content Enabled",
  "CMTeasable_localSettings.gatedContent.enabled_tooltip": "",
  "CMTeasable_localSettings.gatedContent.enabled_true_text": "Content is gated by a form.",
  "CMTeasable_localSettings.gatedContent.form_empyText": "Add form here.",
  "CMTeasable_localSettings.gatedContent.form_text": "Gated Content Form",
  "CMTeasable_localSettings.gatedContent.form_tooltip": "Gated Content Form",
};

export default CUGStudioPlugin_properties;
