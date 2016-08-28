package br.com.twutils.twitter.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Query.Unit;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import br.com.twutils.twitter.TwitterConsumer;
import br.com.twutils.utils.DefaultValues;
import br.com.twutils.utils.PropertiesHandler;
import br.com.twutils.vo.TweetVO;

/**
 * 
 * @author Adail Carvalho
 * @since 1.0.1
 */
public class TwitterConsumerImpl implements TwitterConsumer {
	
	private static final Logger logger = Logger.getLogger(TwitterConsumerImpl.class);
	
	private static Twitter twitter;
	private String accessToken;
	private String accessSecret;
	private String consumerKey;
	private String consumerSecret;
	
	private String output;
	
	private File outputFile;
	
	PropertiesHandler properties;
	
	public TwitterConsumerImpl() {
		properties = new PropertiesHandler();
		loadTokens();
		twitter = authApplicationAccess();
	}
	
	public TwitterConsumerImpl(String accessToken, String accessSecret, String consumerKey, 
			String consumerSecret) {
		this.accessToken = accessToken;
		this.accessSecret = accessSecret;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		authApplicationAccess();
	}
	
	@SuppressWarnings("deprecation")
	public Twitter authApplicationAccess() {
		Twitter twitter = null;
		try {
			AccessToken accessTk;
			twitter = TwitterFactory.getSingleton();
			accessTk = new AccessToken(accessToken, accessSecret);
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			twitter.setOAuthAccessToken(accessTk);
		} catch (Exception e1) {
			logger.info("Unable to authenticate application. ");
			throw new RuntimeException(e1.getMessage());
		}
		return twitter;
	}
	
	public void loadTokens() {
		Map<String, String> twitterProperties = properties.getTwitterProperties(); 
		this.accessToken = twitterProperties.get("accessToken");
		this.accessSecret = twitterProperties.get("accessSecret");
		this.consumerKey = twitterProperties.get("consumerKey");
		this.consumerSecret = twitterProperties.get("consumerSecret");
	}
	
	public List<TweetVO> searchGeoTweetsFromStream(double latitude, double longitude, double radius,
			String unit, String... keywords) throws TwitterException {
		StringBuilder rawQuery = new StringBuilder();
		List<TweetVO> tweetsListVO = new ArrayList<TweetVO>();

		for (int i = 0; i < keywords.length; i++) {
			rawQuery.append("(" + keywords[i] + ")");
			if (i < keywords.length - 1) {
				rawQuery.append(" OR ");
			}
		}

		Query query = new Query(rawQuery.toString());
		GeoLocation location = new GeoLocation(latitude, longitude);

		if (unit != "mi") {
			query.setGeoCode(location, radius, Unit.km);
		} else {
			query.setGeoCode(location, radius, Unit.mi);
		}

		query.setCount(DefaultValues.DEFAULT_COUNT);
		return searchTweetsFromStream(query, 1L, tweetsListVO);
	}
	
	@SuppressWarnings("unchecked")
	public List<TweetVO> searchTweetsFromStream(Query query, Long sinceId, List<TweetVO> tweetsListVO) {
		QueryResult result;
		List<TweetVO> listWithTweets = tweetsListVO;
		query.setCount(DefaultValues.DEFAULT_COUNT);
		query.setSinceId(sinceId);

		try {
			result = twitter.search(query);
			sinceId = result.getMaxId();

			for (Status status : result.getTweets()) {
				TweetVO twitterVO = new TweetVO();
				Double lat = status.getGeoLocation() == null ? 0.0 : status.getGeoLocation().getLatitude();
				Double lon = status.getGeoLocation() == null ? 0.0 : status.getGeoLocation().getLongitude();
				twitterVO.setTweetId(status.getId());
				twitterVO.setTweetText(status.getText());
				twitterVO.setUserScreenName(status.getUser().getName());
				twitterVO.setRetweetCount(status.getRetweetCount());
				twitterVO.setFavouriteCount(status.getFavoriteCount());
				twitterVO.setLatitude(lat);
				twitterVO.setLongitude(lon);
				twitterVO.setCreatedAt(status.getCreatedAt());
				
				//WriteToFile Test
				JSONObject tweet = new JSONObject();
				tweet.put("id", status.getId());
				tweet.put("text", status.getText());
				tweet.put("username", status.getUser().getScreenName());
				tweet.put("latitude", lat);
				tweet.put("longitude", lon);
				tweet.put("created-at", status.getCreatedAt());
				tweet.put("retweetsNum", status.getRetweetCount());
				tweet.put("favourite", status.getFavoriteCount());
				FileUtils.writeStringToFile(outputFile, tweet.toJSONString() + "\n" , true);
				
				listWithTweets.add(twitterVO);
			}
			Thread.sleep(DefaultValues.DEFAULT_THREAD_WAIT_TIME);
			searchTweetsFromStream(query, sinceId, tweetsListVO);
		} catch (TwitterException e1) {
			logger.info("Could not get tweets from stream.");
			throw new RuntimeException(e1);
		} catch (InterruptedException e2) {
			throw new RuntimeException(e2);
		} catch (IOException e3) {
			logger.info("Path to output file not found. ");
			throw new RuntimeException(e3);
		}
		return listWithTweets;
	}
	
	public List<TweetVO> searchTweetsFromStream(String output, String... keywords) throws TwitterException {
		StringBuilder rawQuery = new StringBuilder();
		List<TweetVO> tweetsListVO = new ArrayList<TweetVO>();
		
		this.setOutputFile(output);
		for (int i = 0; i < keywords.length; i++) {
			rawQuery.append("(" + keywords[i] + ")");
			if (i < keywords.length - 1) {
				rawQuery.append(" OR ");
			}
		}
		Query query = new Query(rawQuery.toString());
		return searchTweetsFromStream(query, 1L, tweetsListVO);
	}
	
	public void setOutputFile(String output) {
		this.outputFile = new File(output + "\\tweets_data.json");
	}
}