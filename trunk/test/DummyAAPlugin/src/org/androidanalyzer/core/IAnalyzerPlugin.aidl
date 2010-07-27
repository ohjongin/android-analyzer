package org.androidanalyzer.core;

import org.androidanalyzer.core.Data;

// 	Interface to be used with Analyzer Plugin

interface IAnalyzerPlugin {
	
	String getName();
    
    Data startAnalysis();
    
    void stopAnalysis();
    
    long getTimeout();
    
    void setDebugEnabled(boolean enabled);
    
}