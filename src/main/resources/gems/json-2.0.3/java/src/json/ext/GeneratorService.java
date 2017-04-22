/*
 * This code is copyrighted work by Daniel Luz <dev at mernen dot com>.
 *
 * Distributed under the Ruby license: https://www.ruby-lang.org/en/about/license.txt
 */
package json.ext;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.runtime.load.BasicLibraryService;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * The service invoked by JRuby's {@link org.jruby.runtime.load.LoadService LoadService}.
 * Defines the <code>JSON::Ext::Generator</code> module.
 * @author mernen
 */
public class GeneratorService implements BasicLibraryService {
    public boolean basicLoad(Ruby runtime) throws IOException {
        runtime.getLoadService().require("json/common");
        RuntimeInfo info = RuntimeInfo.initRuntime(runtime);

        info.jsonModule = new WeakReference<RubyModule>(runtime.defineModule("JSON"));
        RubyModule jsonExtModule = info.jsonModule.get().defineModuleUnder("Ext");
        RubyModule generatorModule = jsonExtModule.defineModuleUnder("Generator");

        RubyClass stateClass =
            generatorModule.defineClassUnder("State", runtime.getObject(),
                                             GeneratorState.ALLOCATOR);
        stateClass.defineAnnotatedMethods(GeneratorState.class);
        info.generatorStateClass = new WeakReference<RubyClass>(stateClass);

        RubyModule generatorMethods =
            generatorModule.defineModuleUnder("GeneratorMethods");
        GeneratorMethods.populate(info, generatorMethods);

        return true;
    }
}
