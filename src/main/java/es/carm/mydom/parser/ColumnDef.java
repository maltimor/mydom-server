package es.carm.mydom.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ColumnDef {
	private String name="";
	private boolean key=false;
	private boolean link=false;
	private String title = "";
	private BeanMethod formula=null;
	private String item = "";
	private boolean hidden = false;
	private boolean separatemultiplevalues = false;	
	private String listseparator = "";
	private String itemType = "text";
	private boolean fullText = true;

	
	private String headerFont = "";
	private String align = "";
	private boolean showasicons = false;
	private boolean twisties = false;
	private String sort = "";
	private boolean sortnocase = false;
	private boolean sortnoacent = false;

	private boolean categorized = false;
	private String resort = "";
	private String totals = "";
	private boolean showaslinks = false;
	private String extattrs = "";

	public String toString() {
		String res = "|";
		res += " name:" + name;
		res += " key:" + key;
		res += " item:" + item;
		res += " formula:" + formula;
		res += " title:" + title;
		res += " headerFont:" + headerFont;
		res += " align:" + align;
		res += " showasicons:" + showasicons;
		res += " hidden:" + hidden;
		res += " twisties:" + twisties;
		res += " sort:" + sort;
		res += " sortnocase:" + sortnocase;
		res += " sortnoacent:" + sortnoacent;
		res += " separatemultiplevalues:" + separatemultiplevalues;
		res += " categorized:" + categorized;
		res += " resort:" + resort;
		res += " totals:" + totals;
		res += " showaslinks:" + showaslinks;
		res += " listseparator:" + listseparator;
		res += " itemtype:" + itemType;
		res += " extattrs:" + extattrs;
		return res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isShowasicons() {
		return showasicons;
	}

	public void setShowasicons(boolean showasicons) {
		this.showasicons = showasicons;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isTwisties() {
		return twisties;
	}

	public void setTwisties(boolean twisties) {
		this.twisties = twisties;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isSortnocase() {
		return sortnocase;
	}

	public void setSortnocase(boolean sortnocase) {
		this.sortnocase = sortnocase;
	}

	public boolean isSortnoacent() {
		return sortnoacent;
	}

	public void setSortnoacent(boolean sortnoacent) {
		this.sortnoacent = sortnoacent;
	}

	public boolean isSeparatemultiplevalues() {
		return separatemultiplevalues;
	}

	public void setSeparatemultiplevalues(boolean separatemultiplevalues) {
		this.separatemultiplevalues = separatemultiplevalues;
	}

	public boolean isCategorized() {
		return categorized;
	}

	public void setCategorized(boolean categorized) {
		this.categorized = categorized;
	}

	public String getResort() {
		return resort;
	}

	public void setResort(String resort) {
		this.resort = resort;
	}

	public String getTotals() {
		return totals;
	}

	public void setTotals(String totals) {
		this.totals = totals;
	}

	public boolean isShowaslinks() {
		return showaslinks;
	}

	public void setShowaslinks(boolean showaslinks) {
		this.showaslinks = showaslinks;
	}

	public String getListseparator() {
		return listseparator;
	}

	public void setListseparator(String listseparator) {
		this.listseparator = listseparator;
	}

	public String getExtattrs() {
		return extattrs;
	}

	public void setExtattrs(String extattrs) {
		this.extattrs = extattrs;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public BeanMethod getFormula() {
		return formula;
	}

	public void setFormula(BeanMethod formula) {
		this.formula = formula;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHeaderFont() {
		return headerFont;
	}

	public void setHeaderFont(String headerFont) {
		this.headerFont = headerFont;
	}

	public boolean isLink() {
		return link;
	}

	public void setLink(boolean link) {
		this.link = link;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean isFullText() {
		return fullText;
	}

	public void setFullText(boolean fullText) {
		this.fullText = fullText;
	}

}
