<SCRIPT LANGUAGE="JavaScript">
<!--
  var aryImages = new Array(12);

  for (x=0; x<3; x++) {
      for (y=0; y<3; y++) {
          i = y * 3 + x;
          aryImages[i] = "logopart."+x+"."+y+".png";
      }
  }
  aryImages[3] = "blank.png";
  for (i=0; i < aryImages.length; i++) {
    var preload = new Image();
    preload.src = aryImages[i];
  }

  var prev = -1;
  var bIndex = 3;

  function swap() {
    old = bIndex;
    while (bIndex == old) {
      rand = Math.random();
      index1 = -1;
      if (rand < 0.25) {
        if (bIndex % 3 != 2) {
          index1 = bIndex + 1;
        }
      } else if (rand < 0.5) {
        if (bIndex % 3 != 0) {
          index1 = bIndex - 1;
        }
      } else if (rand < 0.75) {
        if (bIndex < 6) {
          index1 = bIndex + 3;
        }
      } else {
        if (bIndex > 2) {
          index1 = bIndex - 3;
        }
      }

      if (index1 >= 0 && index1 != prev) {
        index2 = bIndex;
        tmp = aryImages[index2];
        aryImages[index2] = aryImages[index1];
        aryImages[index1] = tmp;
        document['logopart'+index1].src = aryImages[index1];
        document['logopart'+index2].src = aryImages[index2];

        prev = bIndex;
        bIndex = index1;
      }
    }
  }

swap_timer = setInterval('swap();', 333);
//-->
</SCRIPT>
<table cellpadding=0 cellspacing=1 bgcolor="#dddddd" border=0>
  <tr>
    <td><img name="logopart0" src="logopart.0.0.png" border=0></td>
    <td><img name="logopart1" src="logopart.1.0.png" border=0></td>
    <td><img name="logopart2" src="blank.png" border=0></td>
  </tr>
  <tr>
    <td><img name="logopart3" src="logopart.0.1.png" border=0></td>
    <td><img name="logopart4" src="logopart.1.1.png" border=0></td>
    <td><img name="logopart5" src="logopart.2.1.png" border=0></td>
  </tr>
  <tr>
    <td><img name="logopart6" src="logopart.0.2.png" border=0></td>
    <td><img name="logopart7" src="logopart.1.2.png" border=0></td>
    <td><img name="logopart8" src="logopart.2.2.png" border=0></td>
  </tr>
</table>

