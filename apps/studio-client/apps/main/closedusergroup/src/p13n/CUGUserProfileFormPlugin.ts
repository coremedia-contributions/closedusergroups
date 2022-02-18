import CMPersonaForm from "@coremedia-blueprint/studio-client.main.p13n-studio/CMPersonaForm";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import CollapsibleFormPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsibleFormPanel";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import LinkListUtil from "@coremedia/studio-client.main.editor-components/sdk/util/LinkListUtil";
import Component from "@jangaroo/ext-ts/Component";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CUGHelper from "../CUGHelper";
import CUGStudioPlugin_properties from "../CUGStudioPlugin_properties";
import MultiListSelector from "../components/MultiListSelector";

interface CUGUserProfileFormPluginConfig extends Config<NestedRulesPlugin> {
}

class CUGUserProfileFormPlugin extends NestedRulesPlugin {
  declare Config: CUGUserProfileFormPluginConfig;

  constructor(config: Config<CUGUserProfileFormPlugin> = null) {
    super((()=>{
      const bindTo = cast(CMPersonaForm, config.cmp.initialConfig).bindTo;
      return ConfigUtils.apply(Config(CUGUserProfileFormPlugin, {

        rules: [
          Config(DocumentForm, {
            plugins: [
              Config(AddItemsPlugin, {
                recursive: true,
                items: [
                  Config(CollapsibleFormPanel, {
                    itemId: "closedusergroup",
                    title: CUGStudioPlugin_properties.closedusergroup,
                    plugins: [
                      Config(BindDisablePlugin, { bindTo: bindTo }),
                    ],
                    items: [
                      Config(MultiListSelector, {
                        allBeansExpression: CUGHelper.createAvailableGatedContentGroupExpression(bindTo, CUGStudioPlugin_properties.cug_groups_basepaths.split(",")),
                        selectedBeansExpression: LinkListUtil.createLinkValueExpression(bindTo, "profileExtensions.properties.authorities", CUGStudioPlugin_properties.cug_symbol_type, false, ValueExpressionFactory.createFromValue([])),
                        allBeansLabel: CUGStudioPlugin_properties.MultiListSelector_left,
                        allBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_left_emptyText,
                        selectedBeansLabel: CUGStudioPlugin_properties.MultiListSelector_right,
                        selectedBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_right_emptyText,
                      }),
                    ],
                  }),
                ],
                after: [
                  Config(Component, { itemId: "elasticSocial" }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default CUGUserProfileFormPlugin;
