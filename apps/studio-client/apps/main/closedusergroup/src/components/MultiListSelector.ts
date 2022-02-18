import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import Label from "@jangaroo/ext-ts/form/Label";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MultiListSelectorBase from "./MultiListSelectorBase";
import SimpleBeanList from "./SimpleBeanList";

interface MultiListSelectorConfig extends Config<MultiListSelectorBase>, Partial<Pick<MultiListSelector,
  "allBeansExpression" |
  "selectedBeansExpression" |
  "pathExpression" |
  "allBeansLabel" |
  "allBeansEmptyText" |
  "selectedBeansLabel" |
  "selectedBeansEmptyText"
>> {
}

class MultiListSelector extends MultiListSelectorBase {
  declare Config: MultiListSelectorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.base.components.config.multiListSelector";

  constructor(config: Config<MultiListSelector> = null) {
    super((()=> ConfigUtils.apply(Config(MultiListSelector, {

      plugins: [
        Config(PropertyFieldPlugin, { propertyName: config.pathExpression }),
      ],
      items: [
        Config(Container, {
          flex: 1,
          items: [
            Config(Label, {
              text: config.allBeansLabel,
              height: 22,
            }),
            Config(SimpleBeanList, {
              listExpression: this.getAvailableBeansExpression(),
              autoScroll: true,
              itemId: MultiListSelectorBase.ALL,
              emptyText: MultiListSelectorBase.getEmptyText(config.allBeansEmptyText),
            }),
          ],
        }),
        Config(Container, {
          items: [
            Config(Container, {
              padding: "0 6 0 6",
              items: [
                Config(Component, {
                  height: 15,
                  itemId: "inner-container-spacer",
                }),
                Config(IconButton, {
                  iconCls: CoreIcons_properties.collapsing_arrow_right,
                  handler: bind(this, this.handleMoveToRight),
                  plugins: [
                    Config(BindPropertyPlugin, {
                      componentProperty: "disabled",
                      bindTo: this.getAvailableBeansExpression(),
                      transformer: MultiListSelectorBase.isButtonDisabled,
                    }),
                  ],
                }),
                Config(IconButton, {
                  iconCls: CoreIcons_properties.collapsing_arrow_left,
                  handler: bind(this, this.handleMoveToLeft),
                  plugins: [
                    Config(BindPropertyPlugin, {
                      componentProperty: "disabled",
                      bindTo: config.selectedBeansExpression,
                      transformer: MultiListSelectorBase.isButtonDisabled,
                    }),
                  ],
                }),
              ],
              layout: Config(VBoxLayout, { align: "stretch" }),
            }),
          ],
        }),
        Config(Container, {
          flex: 1,
          items: [
            Config(Label, {
              text: config.selectedBeansLabel,
              height: 22,
            }),
            Config(SimpleBeanList, {
              listExpression: config.selectedBeansExpression,
              autoScroll: true,
              itemId: MultiListSelectorBase.SELECT,
              emptyText: MultiListSelectorBase.getEmptyText(config.selectedBeansEmptyText),
            }),
          ],
        }),
      ],
      layout: Config(HBoxLayout, { align: "pack" }),

    }), config))());
  }

  allBeansExpression: ValueExpression = null;

  selectedBeansExpression: ValueExpression = null;

  #pathExpression: string = null;

  get pathExpression(): string {
    return this.#pathExpression;
  }

  set pathExpression(value: string) {
    this.#pathExpression = value;
  }

  #allBeansLabel: string = null;

  get allBeansLabel(): string {
    return this.#allBeansLabel;
  }

  set allBeansLabel(value: string) {
    this.#allBeansLabel = value;
  }

  #allBeansEmptyText: string = null;

  get allBeansEmptyText(): string {
    return this.#allBeansEmptyText;
  }

  set allBeansEmptyText(value: string) {
    this.#allBeansEmptyText = value;
  }

  #selectedBeansLabel: string = null;

  get selectedBeansLabel(): string {
    return this.#selectedBeansLabel;
  }

  set selectedBeansLabel(value: string) {
    this.#selectedBeansLabel = value;
  }

  #selectedBeansEmptyText: string = null;

  get selectedBeansEmptyText(): string {
    return this.#selectedBeansEmptyText;
  }

  set selectedBeansEmptyText(value: string) {
    this.#selectedBeansEmptyText = value;
  }
}

export default MultiListSelector;
