import CMFolderPropertiesForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMFolderPropertiesForm";
import CMPersonaForm from "@coremedia-blueprint/studio-client.main.p13n-studio/CMPersonaForm";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import ModerationImpl from "@coremedia/studio-client.main.es-models/impl/ModerationImpl";
import CustomUserInformationContainer from "@coremedia/studio-client.main.social-studio-plugin/usermanagement/details/CustomUserInformationContainer";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import CUGHelper from "./CUGHelper";
import CUGStudioPlugin_properties from "./CUGStudioPlugin_properties";
import MultiListSelector from "./components/MultiListSelector";
import CUGClosedUserGroupsFolderPropertiesPlugin from "./p13n/CUGClosedUserGroupsFolderPropertiesPlugin";
import CUGUserProfileFormPlugin from "./p13n/CUGUserProfileFormPlugin";

interface CUGStudioPluginRulesConfig extends Config<StudioPlugin> {
}

class CUGStudioPluginRules extends StudioPlugin {
  declare Config: CUGStudioPluginRulesConfig;

  static readonly xtype: string = "com.coremedia.salesdemo.closedusergroup.studio.config.cugStudioPluginRules";

  constructor(config: Config<CUGStudioPluginRules> = null) {
    super(ConfigUtils.apply(Config(CUGStudioPluginRules, {

      rules: [

        Config(CMPersonaForm, {
          plugins: [
            Config(CUGUserProfileFormPlugin),
          ],
        }),

        Config(CMFolderPropertiesForm, {
          plugins: [
            Config(CUGClosedUserGroupsFolderPropertiesPlugin),
          ],
        }),

        Config(CustomUserInformationContainer, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(MultiListSelector, {
                  allBeansExpression: CUGHelper.loadAvailableGroups(CUGStudioPlugin_properties.cug_groups_basepaths.split(",")),
                  selectedBeansExpression: CUGHelper.getAuthoritiesExpression(ModerationImpl.getInstance()),
                  allBeansLabel: CUGStudioPlugin_properties.MultiListSelector_left,
                  allBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_left_emptyText,
                  selectedBeansLabel: CUGStudioPlugin_properties.MultiListSelector_right,
                  selectedBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_right_emptyText,
                }),
              ],
            }),
          ],
        }),

      ],
      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, CUGStudioPlugin_properties),
        }),
      ],

    }), config));
  }
}

export default CUGStudioPluginRules;
