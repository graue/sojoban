(ns sojoban.levels.yoshio
  "Automatically generated levels by Yoshio Murase, from
  http://www.ne.jp/asahi/ai/yoshio/sokoban/auto52/index.html"
  (:require [sojoban.read :refer [ascii-level-to-board]]))

(def yoshio-levels
  ^{:author "Yoshio Murase"
    :title "52 Maps Generated Automatically"}
  (->>
       ["########
         ###  . #
         ## * # #
         ## .$  #
         ##  #$##
         ### @ ##
         ########
         ########"
        "########
         ##  .@ #
         ## #.# #
         ##   $ #
         ##.$$ ##
         ##  ####
         ########
         ########"
        "########
         #### @##
         #  *$ ##
         #     ##
         ## .####
         ##$ ####
         ## .####
         ########"
        "########
         ##.###.#
         ## #  .#
         ## $$ @#
         ##  $  #
         ##  #  #
         ##  ####
         ########"
        "########
         #### @##
         ####   #
         #. #$$ #
         #     ##
         #.  $###
         ##.  ###
         ########"
        "########
         # ..####
         # $    #
         #  #$# #
         # @ .$ #
         ########
         ########
         ########"
        "########
         ###  .##
         # $ # ##
         # *$  ##
         # .#@ ##
         #    ###
         #   ####
         ########"
        "########
         ########
         #.  @.##
         #  $# ##
         # # $. #
         #   $# #
         ####   #
         ########"
        "########
         #. .####
         #.#$$ ##
         #   @ ##
         # $#  ##
         ##   ###
         ########
         ########"
        "########
         #.  ####
         # #   ##
         # . # ##
         # $*$ ##
         ##@ ####
         ##  ####
         ########"
        "########
         ########
         #.   . #
         # # #  #
         #@$  $.#
         ##### $#
         #####  #
         ########"
        "########
         #  #####
         #  #####
         # .*   #
         ##$    #
         ## #$###
         ##. @###
         ########"
        "########
         ## @ ###
         ## .   #
         #. $.$ #
         ##$# ###
         ##   ###
         ########
         ########"
        "########
         ##   ###
         # $# ###
         # . @###
         # *   ##
         ## #$ ##
         ##.  ###
         ########"
        "########
         ########
         ##  ####
         #..$  .#
         # #$ $ #
         #@  #  #
         #####  #
         ########"
        "########
         ##  .@##
         ##   $.#
         ####*# #
         ##     #
         #  $  ##
         #   ####
         ########"
        "########
         ##@ ####
         ##  ####
         ##. ####
         # $$. .#
         #  $ ###
         ###  ###
         ########"
        "########
         ########
         ##.  ###
         ## # ###
         ## *$  #
         ##  $. #
         ##  @###
         ########"
        "########
         ########
         ###   ##
         ### #.##
         ###  .##
         #@ $$ ##
         #  .$ ##
         ########"
        "########
         #   @###
         # $# ###
         # * $  #
         #   ## #
         ##.  . #
         ###   ##
         ########"
        "########
         ##   @##
         ##  #  #
         ##.  $ #
         ## $$#.#
         ####  .#
         ########
         ########"
        "########
         ########
         ###. ###
         # .  ###
         #   $$ #
         ## . $@#
         ########
         ########"
        "########
         ##@.  ##
         # $$* ##
         #  #  ##
         #  #  .#
         #### # #
         ####   #
         ########"
        "########
         #####  #
         #####$.#
         ###  . #
         ###  #.#
         # $  $ #
         #   #@ #
         ########"
        "########
         #  .####
         # $.. ##
         #  ##$##
         ##  #  #
         ##$   @#
         ##  ####
         ########"
        "########
         ###  ###
         ###  ###
         ### .. #
         #  $#  #
         #  .$$ #
         #### @ #
         ########"
        "########
         #   ####
         # # *@##
         #  *   #
         ###$   #
         ###   .#
         ########
         ########"
        "########
         ### .  #
         # $@#. #
         #  $# ##
         #  *  ##
         ##  # ##
         ###   ##
         ########"
        "########
         ########
         ########
         ##  ####
         #     ##
         #  #$$@#
         #  . *.#
         ########"
        "########
         ##@    #
         #. #   #
         # $$$.##
         # .#  ##
         #  #####
         ########
         ########"
        "########
         #      #
         # # ##*#
         # #@ $ #
         #.$ .  #
         #####  #
         #####  #
         ########"
        "########
         ##@   ##
         ###$   #
         ### .  #
         # $ #$##
         # .  .##
         ####  ##
         ########"
        "########
         #   ####
         #  $  ##
         ##$$ .##
         ##@ . ##
         ### # ##
         ###  .##
         ########"
        "########
         #   ####
         # $$   #
         # .#.  #
         #  ## ##
         #  ##$##
         # @  .##
         ########"
        "########
         ########
         ########
         # .  ###
         # .# ###
         # @$$  #
         # $.   #
         ########"
        "########
         # @.#  #
         # .$ . #
         #  #$  #
         #  $  ##
         ###  ###
         ###  ###
         ########"
        "########
         #    . #
         # $  $@#
         #.$.####
         #  #####
         #  #####
         #  #####
         ########"
        "########
         # .  ###
         #  #@###
         #  $ ###
         ##$#  ##
         #   # ##
         #. *  ##
         ########"
        "########
         ########
         #### . #
         # *@ . #
         # $ #  #
         # #  $ #
         #   ####
         ########"
        "########
         ########
         ########
         ###  ###
         # .. $.#
         #  $$ @#
         ####   #
         ########"
        "########
         ########
         #####@ #
         ##### .#
         # $ $ $#
         #   .  #
         ### .  #
         ########"
        "########
         #   #  #
         # #.$ $#
         #   $  #
         #####. #
         ###   @#
         ###   .#
         ########"
        "########
         ####@ ##
         ###  ..#
         ## $#$##
         #   $. #
         #  #   #
         #    ###
         ########"
        "########
         #   @###
         # $$####
         # $ .  #
         ## #.# #
         #.   # #
         #      #
         ########"
        "########
         ####  ##
         #### $##
         # @$.  #
         # ##   #
         #   ## #
         #   * .#
         ########"
        "########
         #### @ #
         ####   #
         ## $ $##
         ## $  ##
         #.  # ##
         #..   ##
         ########"
        "########
         ########
         ####. @#
         #  .$  #
         # #  ###
         # $ $ .#
         ####   #
         ########"
        "########
         ########
         #  .# @#
         # # $  #
         # $.#$ #
         ## .   #
         ##  ####
         ########"
        "########
         ########
         ##     #
         ##.## .#
         ##*  $@#
         ##  #$ #
         ##  #  #
         ########"
        "########
         #. #####
         # $#####
         #  #####
         # .$ @ #
         # .$ # #
         ###    #
         ########"
        "########
         #      #
         # #$   #
         # $ @#.#
         ##$#.  #
         ##    .#
         ########
         ########"
        "########
         #  . ###
         #    ###
         # #$$. #
         #.  ## #
         #@$ ## #
         ###    #
         ########"]
      (mapv ascii-level-to-board)
      (#(with-meta % {:author "Yoshio Murase"
                      :title "52 Maps Generated Automatically"}))))
