define('messages', ['i18n'], function (I18n) {
  // Spanish (Argentina)
  I18n.defaultLocale = 'en_US';
  I18n.locale = 'en_US';
  I18n.translations = I18n.translations || {};
  I18n.translations['es_AR'] = {
    "hello": "Hola",
    "formatted": "Hi {{arg0}}"
  };

  // English (United States)
  I18n.defaultLocale = 'en_US';
  I18n.locale = 'en_US';
  I18n.translations = I18n.translations || {};
  I18n.translations['en_US'] = {
    "hello": "Hi",
    "formatted": "Hi {{arg0}}"
  };

};
