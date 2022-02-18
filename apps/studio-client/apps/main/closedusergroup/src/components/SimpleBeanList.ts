import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindSelectionPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindSelectionPlugin";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import BlueprintComponents_properties from "@coremedia/studio-client.main.bpbase-studio-components/BlueprintComponents_properties";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PropertyFieldContextMenu from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/PropertyFieldContextMenu";
import SitesService from "@coremedia/studio-client.multi-site-models/SitesService";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface SimpleBeanListConfig extends Config<GridPanel>, Partial<Pick<SimpleBeanList,
  "listExpression"
>> {
}

class SimpleBeanList extends GridPanel {
  declare Config: SimpleBeanListConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.base.components.config.simpleBeanList";

  #sitesService: SitesService = null;

  #selectedBeansVE: ValueExpression = null;

  // called by generated constructor code
  #__initialize__(config: Config<SimpleBeanList>): void {
    this.#sitesService = editorContext._.getSitesService();
    this.#selectedBeansVE = ValueExpressionFactory.createFromValue([]);
  }

  constructor(config: Config<SimpleBeanList> = null) {
    super((()=>{
      this.#__initialize__(config);
      return ConfigUtils.apply(Config(SimpleBeanList, {
        sortableColumns: false,
        enableColumnHide: false,
        forceFit: true,

        plugins: [
          Config(BindListPlugin, {
            bindTo: config.listExpression,
            fields: [
              Config(DataField, {
                name: "name",
                ifUnreadable: ContentLocalizationUtil.formatNotReadableName,
                mapping: "name",
              }),
              Config(DataField, {
                name: "site",
                mapping: "",
                convert: bind(this.#sitesService, this.#sitesService.getSiteNameFor),
              }),
            ],
          }),
          Config(BindSelectionPlugin, { selectedValues: this.#selectedBeansVE }),
          Config(ContextMenuPlugin, { contextMenu: Config(PropertyFieldContextMenu, { selectedItemsVE: this.#selectedBeansVE }) }),
        ],

        columns: [
          Config(Column, {
            dataIndex: "name",
            width: 60,
            header: BlueprintComponents_properties.ContextListPanel_list_header_name,
          }),
          Config(Column, {
            dataIndex: "site",
            width: 40,
            header: BlueprintComponents_properties.ContextListPanel_list_header_site,
            tpl: "<span>{site}<\/span>",
          }),
        ],
        selModel: new RowSelectionModel({ mode: "MULTI" }),

      }), config);
    })());
  }

  /**
   * A value expression evaluating to an Array of Beans that are to be shown.
   */
  listExpression: ValueExpression = null;
}

export default SimpleBeanList;
