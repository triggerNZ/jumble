<SCRIPT LANGUAGE="JavaScript">
<!--
  var aryImages = new Array(12);

  for (x=0; x<4; x++) {
      for (y=0; y<3; y++) {
          i = y * 4 + x;
          aryImages[i] = "r2."+x+"."+y+".png";
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
        if (bIndex % 4 != 3) {
          index1 = bIndex + 1;
        }
      } else if (rand < 0.5) {
        if (bIndex % 4 != 0) {
          index1 = bIndex - 1;
        }
      } else if (rand < 0.75) {
        if (bIndex < 8) {
          index1 = bIndex + 4;
        }
      } else {
        if (bIndex > 3) {
          index1 = bIndex - 4;
        }
      }

      if (index1 >= 0 && index1 != prev) {
        index2 = bIndex;
        tmp = aryImages[index2];
        aryImages[index2] = aryImages[index1];
        aryImages[index1] = tmp;
        document['rt'+index1].src = aryImages[index1];
        document['rt'+index2].src = aryImages[index2];

        prev = bIndex;
        bIndex = index1;
      }
    }
  }

swap_timer = setInterval('swap();', 333);
//-->
</SCRIPT>
<a href="http://giger.nz.reeltwo.com/">
    <table cellpadding=0 cellspacing=1 bgcolor="#dddddd" border=0>
        <tr>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt0" src="r2.0.0.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt1" src="r2.1.0.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt2" src="r2.2.0.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt3" src="blank.png" border=0></a></td>
        </tr>
        <tr>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt4" src="r2.0.1.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt5" src="r2.1.1.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt6" src="r2.2.1.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt7" src="r2.3.1.png" border=0></a></td>
        </tr>
        <tr>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt8" src="r2.0.2.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt9" src="r2.1.2.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt10" src="r2.2.2.png" border=0></a></td>
          <td><a href="http://giger.nz.reeltwo.com/"><img name="rt11" src="r2.3.2.png" border=0></a></td>
        </tr>
    </table>
</a>

