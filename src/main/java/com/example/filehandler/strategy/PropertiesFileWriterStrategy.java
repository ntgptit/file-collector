package com.example.filehandler.strategy;

import java.util.List;
import java.util.regex.Pattern;

public class PropertiesFileWriterStrategy extends FileWriterStrategy {
	private static final Pattern PROPERTIES_COMMENT_PATTERN = Pattern.compile("^\\s*#.*$");

	public PropertiesFileWriterStrategy(List<String> folderPaths) {
		super(folderPaths);
	}

	@Override
	protected boolean isCommentLine(String line) {
		return PROPERTIES_COMMENT_PATTERN.matcher(line).matches();
	}
}