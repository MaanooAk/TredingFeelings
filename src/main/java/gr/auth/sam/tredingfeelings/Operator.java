
package gr.auth.sam.tredingfeelings;

import gr.auth.sam.tredingfeelings.util.ProgressBar;


public abstract class Operator {

    protected final Params params;

    protected ProgressBar progress;

    public Operator(Params params) {
        this.params = params;
    }

    public abstract void start();
    
}
