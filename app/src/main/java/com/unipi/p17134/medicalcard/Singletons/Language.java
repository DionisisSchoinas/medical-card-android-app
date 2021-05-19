package com.unipi.p17134.medicalcard.Singletons;

public class Language {
    private String language;
    private String languageLocale;

    public Language(String languageLocale) {
        this.language = null;
        this.languageLocale = languageLocale;
    }

    public Language(String language, String languageLocale) {
        this.language = language;
        this.languageLocale = languageLocale;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public String toString() {
        return language;
    }
}
