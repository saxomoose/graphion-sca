const miniCssExtractPlugin = require('mini-css-extract-plugin');
const terserPlugin = require('terser-webpack-plugin');
const cssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const path = require("path");

module.exports = (env, argv) => ({
    mode: argv.mode === 'production' ? 'production' : 'development',
    entry: './src/main/resources/static/js/app.js',
    plugins: [new miniCssExtractPlugin({filename: "css/bundle.css"})],
    output: {
        path: path.resolve(__dirname, './build/resources/main/static'),
        filename: 'js/bundle.js'
    },
    optimization: {
        minimize: true,
        minimizer: [
            new terserPlugin(),
            new cssMinimizerPlugin()
        ]
    },
    devtool: argv.mode === 'production' ? false : 'eval-source-map',
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
                        loader: argv.mode === 'production' ? miniCssExtractPlugin.loader : 'style-loader'
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
});