module.exports = {
    "env": {
        "browser": true
    },
    "extends": [
        "semistandard"
    ],
    "globals": {
        "bootstrap": "true"
    },
    "plugins": [
        "html"
    ],
    "rules": {
        "indent": ["error", 4],
        "no-template-curly-in-string": "off",
        "quotes": [2, "double"],
        "quote-props": [2, "always"]
    }
};
