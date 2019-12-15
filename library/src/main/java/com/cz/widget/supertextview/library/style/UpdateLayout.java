package com.cz.widget.supertextview.library.style;


/**
 * The classes that affect character-level text formatting in a way that
 * triggers a text layout update when one is added or removed must implement
 * this interface.  This interface also includes {@link UpdateAppearance}
 * since such a change implicitly also impacts the appearance.
 */
public interface UpdateLayout extends UpdateAppearance { }
