import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ContentLookupUtil from "@coremedia/studio-client.main.bpbase-studio-components/util/ContentLookupUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import { as } from "@jangaroo/runtime";

class CUGHelper {

  static loadAvailableGroups(gatedUserGroupsPaths: Array<any>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(CUGHelper.#computeAvailableGatedUserGroups, gatedUserGroupsPaths);
  }

  static createAvailableGatedContentGroupExpression(referenceContentValueExpression: ValueExpression, gatedUserGroupsPaths: Array<any>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(CUGHelper.#computeAvailableGatedUserGroupsUsingBasePath, referenceContentValueExpression, gatedUserGroupsPaths);
  }

  static getAuthoritiesExpression(context: any): ValueExpression {
    return ValueExpressionFactory.createTransformingValueExpression(ValueExpressionFactory.create("userAdministration.edited.authorities", context), CUGHelper.#transformAuthorities, CUGHelper.#reverseTransformAuthorities, []);
  }

  static #transformAuthorities(value: any) {
    if (value == null || value == undefined) {
      return [];
    }
    return value;
  }

  static #reverseTransformAuthorities(value: any) {
    return value;
  }

  static #computeAvailableGatedUserGroups(gatedContentBasePaths: Array<any>): Array<any> {
    let result = [];
    editorContext._.getSitesService().getSites().forEach((site: Site): void => {
      if (site.getMasterSite() == null) {
        const elements = CUGHelper.#findContentsOfTypeInPaths(gatedContentBasePaths, ["CMSymbol"], site);
        if (elements) {
          result = result.concat(elements);
        }
      }
    });
    if (!result) {
      return undefined;
    }
    return result;
  }

  static #findContentsOfTypeInPaths(paths: Array<any>, contentTypes: Array<any>, site: Site): Array<any> {
    let result = [];

    if (site === undefined) {
      result = undefined;
    }
    const contentRepository = editorContext._.getSession().getConnection().getContentRepository();
    const siteRootFolder: Content = site && site.getSiteRootFolder();
    for (let path of paths as string[]) {
      path = CUGHelper.#resolvePath(path, site);
      const folder = contentRepository.getChild(path["trim"](), null, siteRootFolder);
      if (folder === undefined) {
        result = undefined;
      } else if (folder) {
        const childDocuments = folder.getChildDocuments();
        if (childDocuments === undefined) {
          result = undefined;
        } else {
          for (const c of childDocuments as Content[]) {
            const childContentType = c.getType();
            if (childContentType === undefined) {
              result = undefined;
            } else if (result && contentTypes.indexOf(childContentType.getName()) !== -1) {
              result.push(c);
            }
          }
        }
      }
    }
    return result;
  }

  static #computeAvailableGatedUserGroupsUsingBasePath(referenceContentValueExpression: ValueExpression, gatedContentBasePaths: Array<any>): Array<any> {
    const referenceContent = as(referenceContentValueExpression.getValue(), Content);
    if (!referenceContent) {
      return undefined;
    }
    if (!referenceContent.getType().getName()) {
      return undefined;
    }

    const site = editorContext._.getSitesService().getSiteFor(referenceContent);
    if (!site) {
      return undefined;
    }
    const gatedContentPaths = [];
    gatedContentBasePaths.forEach((path: string): void => {
      const absolutepath = CUGHelper.#resolvePath(path, site);
      gatedContentPaths.push(absolutepath);
    });
    const gatedContents = ContentLookupUtil.findContentsOfTypeInPaths(gatedContentPaths, ["CMSymbol"], referenceContent);
    if (!gatedContents) {
      return undefined;
    }
    return gatedContents;
  }

  /**
   * Compute the absolute path
   */
  static #resolvePath(path: string, site: Site): string {
    //the path is already absolute
    if (path.indexOf("/") === 0) {
      return path;
    }
    //path relative to the site
    const sitesService = editorContext._.getSitesService();
    const root = sitesService.getSiteRootFolder(site.getId());
    return root.getPath() + "/" + path;
  }
}

export default CUGHelper;
