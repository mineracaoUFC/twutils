package br.com.twutils;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import br.com.twutils.runner.TwutilsRunner;
import twitter4j.TwitterException;

public class Twutils {
	
	private static final Logger logger = Logger.getLogger(Twutils.class);
	
	private Options options;
	
	private CommandLine cmdLine;
	
	private CommandLineParser cmdParser;
	
	public Twutils(String[] cmdArgs) throws TwitterException {
		options = new Options();
		cmdParser = new BasicParser();
		buildOptions(cmdArgs);
	}
	
	@SuppressWarnings("static-access")
	public void buildOptions(String[] cmdArgs) throws TwitterException {
		Option strTweetsOpt = OptionBuilder.withArgName("tt")
				.withLongOpt(TwutilsOptions.TWEETS_FROM_STREAM)
				.withDescription("Collect tweets that contains the given keywords. ")
				.hasArg(true)
				.isRequired(true)
				.create();
		
		Option outputOpt = OptionBuilder.withArgName("out")
				.withLongOpt(TwutilsOptions.OUTPUT_PATH)
				.withDescription("Output dir of tweets. ")
				.hasArg(true)
				.isRequired(true)
				.create();
		
		Option helpOpt = OptionBuilder.withArgName("h")
				.withLongOpt(TwutilsOptions.HELP)
				.withDescription("Show help. ")
				.hasArg(false)
				.create();
		
		options.addOption(strTweetsOpt);
		options.addOption(outputOpt);
		options.addOption(helpOpt);
		
		try {
			cmdLine = cmdParser.parse(options, cmdArgs);
			run();
		} catch (ParseException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	public void help() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Twutils", options);
	}
	
	public int run() throws TwitterException {
		TwutilsRunner runner = new TwutilsRunner();
		if (cmdLine.hasOption(TwutilsOptions.TWEETS_FROM_STREAM)) {
			String[] keywords =  cmdLine.getOptionValues(TwutilsOptions.TWEETS_FROM_STREAM);
			String[] outputDir = cmdLine.getOptionValues(TwutilsOptions.OUTPUT_PATH);
			String output = outputDir[0];
			
			runner.searchTweetsFromStream(output, keywords);
		}
			 
		if (cmdLine.hasOption(TwutilsOptions.HELP)) {
			
			   
		} else {
			logger.info("The entered option does not exists. ");
			help();
			return -1;
		}

		return 0;
	}
	
	public static void main(String[] args) throws TwitterException {
		int execStat = new Twutils(args)
				.run();
		System.exit(execStat);
	}
}