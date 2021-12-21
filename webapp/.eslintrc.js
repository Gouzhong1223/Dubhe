module.exports = {
  root: true,
  parserOptions: {
    parser: 'babel-eslint',
    sourceType: 'module',
  },
  env: {
    browser: true,
    node: true,
    es6: true,
  },
  extends: [
    'airbnb-base',
    'plugin:vue/recommended',
    'eslint-config-prettier',
    'prettier/vue',
  ],
  plugins: ['import', 'eslint-plugin-prettier'],
  settings: {
    'import/resolver': {
      webpack: {
        config: require.resolve('@vue/cli-service/webpack.config.js'),
      },
    },
  },
  rules: {
    'vue/require-default-prop': 'off',
    'vue/attribute-hyphenation': 'off',
    'no-console': 0,
    'no-param-reassign': 'off',
    'import/extensions': 'off',
    'import/prefer-default-export': 'off',
    'no-shadow': 'off',
    'no-underscore-dangle': 'off',
    'no-unused-expressions': 'off',
    'no-restricted-syntax': 'off',
    'guard-for-in': 'off',
    camelcase: 'off',
    'no-multi-assign': 'off',
  },
}
