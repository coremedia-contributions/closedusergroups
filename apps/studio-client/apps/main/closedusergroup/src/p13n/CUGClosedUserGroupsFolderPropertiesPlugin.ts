import CMFolderPropertiesForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMFolderPropertiesForm";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import CollapsibleFormPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsibleFormPanel";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import LinkListUtil from "@coremedia/studio-client.main.editor-components/sdk/util/LinkListUtil";
import Component from "@jangaroo/ext-ts/Component";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CUGHelper from "../CUGHelper";
import CUGStudioPlugin_properties from "../CUGStudioPlugin_properties";
import MultiListSelector from "../components/MultiListSelector";

interface CUGClosedUserGroupsFolderPropertiesPluginConfig extends Config<NestedRulesPlugin> {
}

class CUGClosedUserGroupsFolderPropertiesPlugin extends NestedRulesPlugin {
  declare Config: CUGClosedUserGroupsFolderPropertiesPluginConfig;

  #cmFolderPropertiesFormConfig: Config<CMFolderPropertiesForm> = null;

  #allBeansExpression: ValueExpression = null;

  #pathExpression: string = null;

  // called by generated constructor code
  #__initialize__(config: Config<CUGClosedUserGroupsFolderPropertiesPlugin>): void {
    this.#cmFolderPropertiesFormConfig = cast(CMFolderPropertiesForm, config.cmp.initialConfig);
    this.#allBeansExpression = CUGHelper.createAvailableGatedContentGroupExpression(this.#cmFolderPropertiesFormConfig.bindTo, CUGStudioPlugin_properties.cug_groups_basepaths.split(","));
    this.#pathExpression = "localSettings.accessAllowedAuthorities";
  }

  constructor(config: Config<CUGClosedUserGroupsFolderPropertiesPlugin> = null) {
    super((()=>{
      this.#__initialize__(config);
      return ConfigUtils.apply(Config(CUGClosedUserGroupsFolderPropertiesPlugin, {

        rules: [
          Config(DocumentForm, {
            itemId: "contentTab",
            plugins: [
              Config(AddItemsPlugin, {
                recursive: true,
                items: [
                  Config(CollapsibleFormPanel, {
                    title: CUGStudioPlugin_properties.closedusergroup,
                    itemId: "closedUserGroups",

                    plugins: [
                      Config(BindVisibilityPlugin, {
                        bindTo: this.#allBeansExpression,
                        transformer: (contents: Array<any>): boolean => contents && contents.length != 0,
                      }),
                    ],
                    items: [
                      Config(MultiListSelector, {
                        allBeansExpression: this.#allBeansExpression,
                        selectedBeansExpression: LinkListUtil.createLinkValueExpression(this.#cmFolderPropertiesFormConfig.bindTo, this.#pathExpression, CUGStudioPlugin_properties.cug_symbol_type, false, ValueExpressionFactory.createFromValue([])),
                        allBeansLabel: CUGStudioPlugin_properties.MultiListSelector_left,
                        allBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_left_emptyText,
                        selectedBeansLabel: CUGStudioPlugin_properties.MultiListSelector_right,
                        selectedBeansEmptyText: CUGStudioPlugin_properties.MultiListSelector_right_emptyText,
                        pathExpression: this.#pathExpression,
                      }),
                    ],
                  }),
                ],
                after: [
                  Config(Component, { itemId: "cmFolderContextsForm" }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default CUGClosedUserGroupsFolderPropertiesPlugin;
