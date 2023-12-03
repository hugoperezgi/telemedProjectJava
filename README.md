# Telemed Project in Java

    (｡◕‿◕｡)

## Pre-configured users:

<b>Admin0-></b> User: `admin` Password: `admin` <br>
<b>Admin1-></b> User: `a` Password: `a` <br>

<b>Patient0-></b> User: `patient` Password: `patient` <br>
<b>Patient1-></b> User: `p` Password: `p` <br>

<b>Doctor0-></b> User: `doctor` Password: `doctor` <br>
<b>Dcotor1-></b> User: `d` Password: `d` <br>

By default, `Patient0` will be linked to `Doctor0`; and `Patient1` to `Doctor1`.

## Random stuff:

>Server will be hosted in `127.0.0.1:50500`

>Server uses threads to attend multiple clients simultaneously (No Selector, each client has a dedicated thread).

>Doctors can only see patients they are linked to.