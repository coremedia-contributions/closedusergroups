import CMFolderPropertiesForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMFolderPropertiesForm";
import CMPersonaForm from "@coremedia-blueprint/studio-client.main.p13n-studio/CMPersonaForm";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import CopyResourceBundleProperties
  from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import CUGStudioPlugin_properties from "./CUGStudioPlugin_properties";
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
