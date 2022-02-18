import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import MultiListSelector from "./MultiListSelector";

interface MultiListSelectorBaseConfig extends Config<Container> {
}

class MultiListSelectorBase extends Container {
  declare Config: MultiListSelectorBaseConfig;

  static readonly ALL: string = "allBeansList";

  static readonly SELECT: string = "selectedBeansList";

  #selectedBeansExp: ValueExpression = null;

  #allBeansExp: ValueExpression = null;

  #availableBeansExp: ValueExpression = null;

  #left: GridPanel = null;

  #right: GridPanel = null;

  constructor(config: Config<MultiListSelector> = null) {
    super((()=>{
      this.#selectedBeansExp = config.selectedBeansExpression;
      this.#allBeansExp = config.allBeansExpression;
      return config;
    })());
  }

  protected override afterRender(): void {
    super.afterRender();
    this.calculateAmount().addChangeListener(bind(this, this.calculateHeight));
  }

  protected calculateHeight(amountOfElementsVE: ValueExpression): void {
    const spacerComponent = as(this.queryById("inner-container-spacer"), Component);
    spacerComponent.setHeight(amountOfElementsVE.getValue() / 2 * 23 + 15);
  }

  protected calculateAmount(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): int => {
      const fve: Array<any> = this.getAvailableBeansExpression().getValue();
      const vfe2: Array<any> = this.#selectedBeansExp.getValue();
      if (Ext.isArray(fve) && Ext.isArray(vfe2)) {
        const itemsOnLeftHandSide: int = fve.length;
        const itemsOnRightHandSide: int = vfe2.length;
        return (itemsOnLeftHandSide > itemsOnRightHandSide ? itemsOnLeftHandSide : itemsOnRightHandSide);
      }
    });
  }

  getAvailableBeansExpression(): ValueExpression {
    if (!this.#availableBeansExp) {
      this.#availableBeansExp = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const allBeans: Array<any> = this.#allBeansExp.getValue();
        const selectedBeans: Array<any> = this.#selectedBeansExp.getValue();
        return MultiListSelectorBase.filter(allBeans, selectedBeans);
      });
    }
    return this.#availableBeansExp;
  }

  protected override initComponent(): void {
    super.initComponent();
    this.#left = as(this.queryById(MultiListSelectorBase.ALL), GridPanel);
    this.#right = as(this.queryById(MultiListSelectorBase.SELECT), GridPanel);

    this.#left.on("dblclick", bind(this, this.handleMoveToRight));
    this.#right.on("dblclick", bind(this, this.handleMoveToLeft));
  }

  static #getSelectedBeans(gp: GridPanel): Array<any> {
    const result = [];
    Ext.each(gp.getSelectionModel().getSelection(), (br: BeanRecord): void => {
      result.push(br.getBean());
    });
    return result;
  }

  protected handleMoveToRight(): void {
    const oldValue: Array<any> = this.#selectedBeansExp.getValue();
    const selectedBeans = MultiListSelectorBase.#getSelectedBeans(this.#left);
    this.#selectedBeansExp.setValue(oldValue ? oldValue.concat(selectedBeans) : selectedBeans);
  }

  protected handleMoveToLeft(): void {
    const oldValue: Array<any> = this.#selectedBeansExp.getValue();
    this.#selectedBeansExp.setValue(MultiListSelectorBase.filter(oldValue, MultiListSelectorBase.#getSelectedBeans(this.#right)));
  }

  protected static filter(items: Array<any>, toBeRemoved: Array<any>): Array<any> {
    if (Ext.isArray(items) && Ext.isArray(toBeRemoved)) {
      return items.filter((item: any): boolean =>
        toBeRemoved.indexOf(item) < 0,
      );
    }
    // default return value is undefined
  }

  protected static getEmptyText(text: string): string {
    return "<div class=\"default-text\"><label style=\"vertical-align: middle;cursor: pointer;\">" + text + "</label></div>";
  }

  protected static isButtonDisabled(items: Array<any>): boolean {
    return Ext.isEmpty(items);
  }
}

export default MultiListSelectorBase;
