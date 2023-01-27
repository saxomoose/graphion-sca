const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TerserWebpackPlugin = require("terser-webpack-plugin");
const CssMinimizerWebpackPlugin = require("css-minimizer-webpack-plugin");
const path = require("path");

module.exports = (env, argv) => ({
    "entry": "./src/main/resources/static/js/app.js",
    "output": {
        "path": path.resolve(__dirname, "./build/resources/main/static"),
        "filename": "js/bundle.min.js"
    },
    "plugins": [new MiniCssExtractPlugin({ "filename": "css/bundle.min.css" })],
    "optimization": {
        "minimize": true,
        "minimizer": [
            new CssMinimizerWebpackPlugin(),
            new TerserWebpackPlugin({ "extractComments": false })
        ]
    },
    // Comment out in prod.
    "devtool": "source-map",
    // "watchOptions": {
    //     "ignored": "/node_modules/"
    // },
    "module": {
        "rules": [
            {
                "test": /\.(js)$/,
                "use": {
                    "loader": "babel-loader",
                    "options": {
                        "presets": ["@babel/preset-env"]
                    }
                }
            },
            {
                "test": /\.(scss)$/,
                "use": [
                    {
                        "loader": MiniCssExtractPlugin.loader
                    },
                    {
                        "loader": "css-loader",
                        "options": {
                            "importLoaders": 2,
                            "sourceMap": true,
                            "url": false
                        }
                    },
                    {
                        "loader": "postcss-loader",
                        "options": {
                            "postcssOptions": {
                                "plugins": [
                                    "autoprefixer"
                                ]
                            },
                            "sourceMap": true
                        }
                    },
                    {
                        "loader": "sass-loader",
                        "options": { "sourceMap": true }
                    }
                ]
            },
            {
                "test": /\.(jpg|jpeg|png|git|svg)$/i,
                "type": "asset/resource"
            }
        ]
    }
});
