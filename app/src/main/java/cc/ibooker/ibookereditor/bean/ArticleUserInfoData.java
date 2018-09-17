package cc.ibooker.ibookereditor.bean;

/**
 * 保存当前用户相对于文章的相关信息
 * 
 * @author 邹峰立
 */
public class ArticleUserInfoData {
	private boolean isZan;// 是否已赞文章
	private boolean isAppreciate;// 是否已喜欢文章

	public ArticleUserInfoData() {
		super();
	}

	public ArticleUserInfoData(boolean isZan, boolean isAppreciate) {
		super();
		this.isZan = isZan;
		this.isAppreciate = isAppreciate;
	}

	public boolean isZan() {
		return isZan;
	}

	public void setZan(boolean isZan) {
		this.isZan = isZan;
	}

	public boolean isAppreciate() {
		return isAppreciate;
	}

	public void setAppreciate(boolean isAppreciate) {
		this.isAppreciate = isAppreciate;
	}

	@Override
	public String toString() {
		return "ArticleUserInfoData [isZan=" + isZan + ", isAppreciate="
				+ isAppreciate + "]";
	}

}
