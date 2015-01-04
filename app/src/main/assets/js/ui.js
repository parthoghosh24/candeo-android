 alert("Hello");
 // Initialize the reader element.
      Monocle.Events.listen(
        window,
        'load',
        function () {
         window.reader = Monocle.Reader('reader'); }

      );