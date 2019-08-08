/**
 * This file/module contains all configuration for the build process.
 */
module.exports = {
  /**
   * The `build_dir` folder is where our projects are compiled during
   * development and the `compile_dir` folder is where our app resides once it's
   * completely built.
   */
  build_dir: '../webapp/src/main/resources/public',
  compile_dir: '../webapp/src/main/resources/static',

  /**
   * This is a collection of file patterns that refer to our app code (the
   * stuff in `src/`). These file paths are used in the configuration of
   * build tasks. `js` is all project javascript, less tests. `ctpl` contains
   * our reusable components' (`src/common`) template HTML files, while
   * `atpl` contains the same, but for our app's code. `html` is just our
   * main HTML file, `less` is our main stylesheet, and `unit` contains our
   * app's unit tests.
   */
  app_files: {
    js: [ 'src/**/*.js', '!src/**/*.spec.js', '!src/assets/**/*.js' ],
    jsunit: [ 'src/**/*.spec.js' ],

    coffee: [ 'src/**/*.coffee', '!src/**/*.spec.coffee' ],
    coffeeunit: [ 'src/**/*.spec.coffee' ],

    atpl: [ 'src/app/**/*.tpl.html' ],
    ctpl: [ 'src/common/**/*.tpl.html' ],

    html: [ 'src/index.html' ],
    less: 'src/less/main.less'
  },

  /**
   * This is a collection of files used during testing only.
   */
  test_files: {
    js: [
      'vendor/angular-mocks/angular-mocks.js'
    ]
  },

  /**
   * This is the same as `app_files`, except it contains patterns that
   * reference vendor code (`vendor/`) that we need to place into the build
   * process somewhere. While the `app_files` property ensures all
   * standardized files are collected for compilation, it is the user's job
   * to ensure non-standardized (i.e. vendor-related) files are handled
   * appropriately in `vendor_files.js`.
   *
   * The `vendor_files.js` property holds files to be automatically
   * concatenated and minified with our project source files.
   *
   * The `vendor_files.css` property holds any CSS files to be automatically
   * included in our app.
   *
   * The `vendor_files.assets` property holds any assets to be copied along
   * with our app's assets. This structure is flattened, so it is not
   * recommended that you use wildcards.
   */
  vendor_files: {
    js: [
      'vendor/angular/angular.js',
	  'vendor/angular-resource/angular-resource.js',
	  'vendor/angular-sanitize/angular-sanitize.js',
	  'vendor/angular-animate/angular-animate.js',
	  'vendor/angular-cookies/angular-cookies.js',
	  'vendor/jquery/dist/jquery.js',
	  'vendor/angular-bootstrap/ui-bootstrap-tpls.min.js',
      'vendor/angular-ui-router/release/angular-ui-router.js',
      'vendor/angular-ui-utils/modules/route/route.js',
	  'vendor/angular-growl-v2/build/angular-growl.js',
	  'vendor/ui-select/dist/select.js',
	  'vendor/ng-flow/dist/ng-flow-standalone.js',
	  'vendor/ng-idle/angular-idle.js',
	  'vendor/angular-loading-bar/build/loading-bar.js',
	  'vendor/ladda/js/spin.js',
	  'vendor/ladda/js/ladda.js',
	  'vendor/angular-ladda/dist/angular-ladda.min.js',
	  'vendor/angular-toggle-switch/angular-toggle-switch.js',
	  'vendor/angular-spinner/angular-spinner.js',
	  'vendor/angular-shims-placeholder/dist/angular-shims-placeholder.js',
	  'vendor/prism/prism.js',
	  'vendor/pdfjs-dist/build/pdf.combined.js',
	  'vendor/selection-model/dist/selection-model.js',
	  'vendor/nsPopover/src/nsPopover.js',
	  'vendor/angular-tooltips/dist/angular-tooltips.min.js',
	  'vendor/findertree/dist/finder-tree.js',
	  'vendor/ngDropover/dist/ngdropover.js',
	  'vendor/angular-clipboard/angular-clipboard.js',
	  'vendor/markdown-js/lib/markdown.js',
	  'vendor/angulartics/dist/angulartics.min.js',
	  'vendor/angulartics-google-analytics/dist/angulartics-ga.min.js',
    ],
    css: [
	  'vendor/bootstrap/dist/css/bootstrap-theme.css',
	  'vendor/angular-growl-v2/build/angular-growl.css',
	  'vendor/angular-loading-bar/build/loading-bar.css',
	  'vendor/ui-select/dist/select.css',
	  'vendor/ladda/dist/ladda-themeless.min.css',
	  'vendor/angular-toggle-switch/angular-toggle-switch.css',
	  'vendor/animate.css/animate.min.css',
	  'vendor/prism/themes/prism.css',
	  'vendor/nsPopover/sass/ns-popover.scss',
	  'vendor/angular-tooltips/dist/angular-tooltips.min.css',
	  'vendor/findertree/dist/finder-tree.css'
    ],
    assets: [
	  'vendor/bootstrap/dist/css/bootstrap-theme.css.map',
	  'vendor/angular-tooltips/dist/angular-tooltips.sourcemap.map'
    ]
  },
};
