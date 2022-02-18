
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
};

export default CUGStudioPlugin_properties;
