# jalsaselector

Version 1.0.0

## What is this?

`jalsaselector` is a very simple Java program for setting the default ALSA
output device by writing a configuration file. It has command-line and
graphical interfaces. It is intended for use on Linux systems that use only
ALSA -- no pulse, no pipewire, no JACK, etc. Whether this program will run at
all on systems other than Linux, it will serve no useful purpose there. 

*Don't use this program on systems with pulse audio -- not only*
*will it not work, it will cause problems.* 

All `jalsaselector` does is write an ALSA configuration file in the user's home
directory, with a specific soundcard name for the defaults. Because 
some users have very complex configuration in the standard
`$HOME/.asoundrc`, `jalsaselector` does not
write this file. Instead, it writes `$HOME/.asounrc_inc`. So, for
any changes made by this file to have any effect, the `$HOME/.asoundrc`
file has to exist, and it has to include `.asoundrc_inc` at some
point.

This inclusion is performed with a line like this in `.asoundrc`:

    </home/[username]/.asoundrc_inc>

`jalsaselector` checks that `.asoundrc` exists, and that it has an
inclusion of this form. However, it won't create or edit 
`.asoundrc` itself. The program will warn if the base set-up is incompatible,
but it will still make the settings the user requests in its own files.

`jalsaelector` is an unsophisticated program, and will fail in complex
set-ups. But it works well enough for my modest purposes.

## Installation

Just copy the JAR file, e.g., `binaries/jalsaselector-1.0.0.jar` to
any convenient place.

## Running

Just execute the JAR file using `java -jar`:

    java -jar binaries/jalsaselector-1.0.0.jar [options]

With no options, the program will display its graphic user interface.

The following command-line options are also available:

*-h, --help*  
Show the help message

*-v, --version*
Show the program version

*-l, --list*
Show the list of cards. The first entry on each line is the short name,
which is the value that other command-line arguments need, and the one 
that gets written to the ALSA configuration file. The rest of the line is the
value actually read from `/proc/asound/cards`, which might provide
useful information to identify the card.

*-s, --set [name]*
Sets the current default to the named card. The program will warn if
the name does not correspond to an entry in `/proc/asound/cards`, but
it will still use it. The name here is the short name, as returned by
the `list` option. 

## Limitations

Probably the most significant limitation is that this program only sets
default _cards_, not default _devices_. It really doesn't work well
for cards that have multiple devices or subdevices.

Also important is that fact that not all programs that support ALSA actually
respect the configuration in `.asoundrc`. Firefox, even when built with
ALSA support, does not. In addition, even programs that do respect this
configuration typically only read it on start-up. You almost certainly
won't be able to
change the audio output device while a program is running. 

Both the command-line and graphical UI try to show the current default card in
their card lists. However, they will only be able to do that if the default was set
by this program. That is, they take the default to be the value currently
stored in the program's properties file, `$HOME/.jalsaselector.props`.

`jalsaselector` attempts to set the "GTK" look-and-feel on the Java GUI, 
because it looks nicer than any of the alternatives available in Linux.
If this fails, it should not stop the program working, but it might be
quite ugly.

Screen position of the program's GUI window is left to the window manager. 
However, there's no way in Java to control where the warning dialog boxes
go. In a dual-monitor set-up, you could have the main window on one monitor,
and dialog boxes on another. Java Swing data from before a time when we
routinely used multiple monitors.

Fonts are likely to be quite small, on high-definition screens. Sorry, but
this isnt easy to fix in a Java Swing program -- not in a way that works
on different Linux desktops, anyway.

## Author and legal

jalsaselector is maintained by Kevin Boone, and is copyright (c)2024
Kevin Boone, distributed under the terms of the GNU Public Licence, v3.0.
There is no warranty of any kind.

