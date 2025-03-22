package com.example.filehandler.strategy;

import java.util.List;
import java.util.regex.Pattern;

public class TypeScriptFileWriterStrategy extends FileWriterStrategy {
	private static final Pattern TS_DOC_PATTERN = Pattern.compile("^\\s*/\\*\\*.*\\*/\\s*$");
	private static final Pattern TS_COMMENT_PATTERN = Pattern.compile("^\\s*//.*$");

	public TypeScriptFileWriterStrategy(List<String> folderPaths) {
		super(folderPaths);
	}

	@Override
	protected boolean isCommentLine(String line) {
		return TS_DOC_PATTERN.matcher(line).matches() || TS_COMMENT_PATTERN.matcher(line).matches();
	}
}
