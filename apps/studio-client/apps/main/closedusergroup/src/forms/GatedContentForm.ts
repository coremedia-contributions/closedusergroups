import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import BooleanPropertyField
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import Customizations_properties from "../Customizations_properties";
import StringPropertyField
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";

interface GatedContentFormConfig extends Config<PropertyFieldGroup> {

}

class GatedContentForm extends PropertyFieldGroup {

  declare Config: GatedContentFormConfig;

  constructor(config: Config<GatedContentForm> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(GatedContentForm, {
      title: Customizations_properties.GatedContentForm_title,
      itemId: "gatedContentForm",
      collapsed: true,
      items: [
        Config(BooleanPropertyField, {
          itemId: "gatedContentEnabled",
          bindTo: config.bindTo,
          propertyName: "localSettings.gatedContent.enabled",
          dontTransformToInteger: true,
        }),
        Config(LinkListPropertyField, {
          itemId: "gatedContentForm",
          bindTo: config.bindTo,
          propertyName: "localSettings.gatedContent.form",
          maxCardinality: 1,
          linkType: "FormEditor"
        }),
        Config(StringPropertyField, {
          itemId: "gatedContentCTAText",
          bindTo: config.bindTo,
          propertyName: "localSettings.gatedContent.ctaText",
        }),
      ]
    }), config));
  }

}

export default GatedContentForm;
