const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__closedusergroup-studio",
    namespace: "com.coremedia.salesdemo.closedusergroup.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.salesdemo.closedusergroup.studio.CUGStudioPluginRules",
        name: "Closed User Groups",
      },
    ],
  },
});
