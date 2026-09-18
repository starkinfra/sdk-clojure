(ns starkinfra.pix-user.statistics
  "The PixUser.Statistics map stores fraud statistics data of a Pix user.

  PixUser.Statistics has no verbs of its own: sdk-python's pixuser/statistics
  module exposes only the Statistics class and the hydration helper PixUser
  uses when reading its `:statistics` field back from the API. Every element
  of that list is a plain map shaped like this namespace's docstring, so there
  is nothing here to call.

  ## Attributes (return-only):
    - `:value` [integer]: aggregated value of the statistic. ex: 3
    - `:type` [string]: type of the statistic. ex: \"infractions\"
    - `:source` [string]: source of the statistic. ex: \"keyManagement\"
    - `:after` [string]: start datetime considered for the statistic aggregation. ex: \"2020-04-23T23:00:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the statistic. ex: \"2020-04-23T23:00:00.000000+00:00\"")
