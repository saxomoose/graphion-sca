const miniCssExtractPlugin = require('mini-css-extract-plugin');
const terserPlugin = require('terser-webpack-plugin');
const cssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const path = require("path");

module.exports = (env, argv) => ({
    mode: 'development',
    entry: './src/main/resources/static/js/app.js',
    output: {
        path: path.resolve(__dirname, './build/resources/main/static'),
        filename: 'js/bundle.js'
    },
    plugins: [new miniCssExtractPlugin({filename: "css/bundle.css"})],
    optimization: {
        minimize: true,
        minimizer: [
            new terserPlugin(),
            new cssMinimizerPlugin()
        ]
    },
    module: {
        rules: [
            {
                test: /\.(js)$/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env'],
                    },
                },
            },
            {
                test: /\.(scss)$/,
                use: [
                    {
                        loader: miniCssExtractPlugin.loader
                    },
                    {
                        loader: 'css-loader',
                        options: {
                            importLoaders: 1,
                            sourceMap: true
                        }
                    },
                    {
                        loader: 'postcss-loader',
                        options: {
                            postcssOptions: {
                                plugins: () => [
                                    require('autoprefixer')
                                ]
                            },
                            sourceMap: true
                        }
                    },
                    {
                        loader: 'sass-loader',
                        options: { sourceMap: true }
                    }
                ]
            }
        ]
    },
    watchOptions: {
        ignored: '/node_modules/'
    }
});