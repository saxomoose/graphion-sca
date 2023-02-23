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
            new TerserWebpackPlugin()
        ]
    },
    "module": {
        "rules": [
            {
                "test": /\.js$/,
                "use": {
                    "loader": "babel-loader",
                    "options": {
                        "presets": ["@babel/preset-env"]
                    }
                }
            },
            {
                "test": /\.scss$/,
                "use": [
                    {
                        "loader": MiniCssExtractPlugin.loader
                    },
                    {
                        "loader": "css-loader"
                    },
                    {
                        "loader": "postcss-loader",
                        "options": {
                            "postcssOptions": {
                                "plugins": [
                                    "autoprefixer"
                                ]
                            }
                        }
                    },
                    {
                        "loader": "sass-loader",
                        "options": {
                            "sassOptions": {
                                "outputStyle": "compressed"
                            }
                        }
                    }
                ]
            },
            {
                "test": /\.(gif|jpeg|jpg|png|svg)$/i,
                "type": "asset/resource"
            }
        ]
    }
});
