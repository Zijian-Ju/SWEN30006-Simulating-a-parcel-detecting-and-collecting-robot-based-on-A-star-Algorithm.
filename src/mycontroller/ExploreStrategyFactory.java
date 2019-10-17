package mycontroller;

/**
 * Singleton Strategy Factory in order to produce strategy
 * @author Zijian Ju; Xu Han; Yuting Cai
 */
public class ExploreStrategyFactory {
	public static ExploreStrategyFactory instance = null;
	
	/**
	 * Get instance of strategy factory
	 * @return ExploreStrategyFactory
	 */
	public static ExploreStrategyFactory getInstance() {
		if ( instance == null )
			instance = new ExploreStrategyFactory();
			return instance;
	}
	
	/**
	 * Get NearestFirstStrategy
	 * @return one instance of NearestFirstStrategy
	 */
	public IExploreStrategy getExploreStrategy() {
		return new NearestFirstStrategy();
	}

}
